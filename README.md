public ResultEnum performTptAndTransactionLimitCheckAsync(
        NbblConfig nbblConfig,
        YamlConfig yamlConfig,
        PaymentRequest request,
        PayTxnMasterTable payTxnMasterTable,
        Map<String, String> parentContextMap,
        Executor virtualThreadExecutor,
        TptLimitDAO tptLimitDAO) throws CustomException {

    long stepStart = System.currentTimeMillis();
    final String referenceId = request.getReferenceId();
    log.info("Payment.7: Performing TPT and transaction limit check for referenceId: {}",
            ESAPI.encoder().encodeForHTML(referenceId));

    // 1. Async call to CS service
    CompletableFuture<CommonServicesResponseDTO<AvailableLimitsResponseDTO>> csresponseFuture =
            CompletableFuture.supplyAsync(
                    () -> obpDownstreamCaller.callLimitService(
                            CommonUtils.getRefId(nbblConfig.getBankcode()),
                            yamlConfig.getCsChannelId(),
                            request.getPlainCustomerId()
                    ),
                    virtualThreadExecutor
            );

    // 2. Wait for mapping future (if needed)
    // mapRequestToMasterFuture.join(); // Uncomment if required

    // 3. Set transaction ID
    request.getPayRequestTransaction().getTransaction().setTxnID(payTxnMasterTable.getTxnId());

    // 4. Execute limit check logic
    CommonServicesResponseDTO<AvailableLimitsResponseDTO> availableLimitsResponseDTO = csresponseFuture.join();

    final TptLimitAudit[] tptLimitAuditHolder = { null };
    ResultEnum tptResult = ResultEnum.FAILURE;
    try {
        log.info("Initiating TPT and Transaction limit check for customerId: {}, referenceId: {}",
                request.getHashCustomerId(), referenceId);

        TptLimitAudit tptLimitAudit = mapTptLimitCSResponseToMasterL1(payTxnMasterTable, availableLimitsResponseDTO, AMPSIssuerConstants.REQPAY);
        tptLimitAuditHolder[0] = tptLimitAudit;

        try {
            if (tptLimitAudit.getTptLimitResult().equals(ResultEnum.FAILURE)) {
                throw new CustomException(Integer.parseInt(tptLimitAudit.getErrorCode()),
                        tptLimitAudit.getErrorMessage());
            }

            String tptLimitStr = tptLimitAudit.getTptLimitValue();
            tptResult = tptLimitAudit.getTptLimitResult();
            BigDecimal tptLimit = StringUtils.hasText(tptLimitStr) ? new BigDecimal(tptLimitStr) : BigDecimal.ZERO;
            if (request.getTransactionAmount().compareTo(tptLimit) > 0) {
                throwTptLimitExceededException(request, tptLimit);
            }
        } catch (CustomException e) {
            payTxnMasterTable.setErrMsg(e.getMessage());
            payTxnMasterTable.setErrCode(String.valueOf(e.getCode()));
            tptLimitAudit.setErrorMessage(e.getMessage());
            tptLimitAudit.setErrorCode(String.valueOf(e.getCode()));
            tptLimitAudit.setTptLimitResult(ResultEnum.FAILURE);
            throw new RuntimeException(e);
        }

        try {
            BigDecimal transactionLimit = yamlConfig.getTransactionLimit();
            if (request.getTransactionAmount().compareTo(transactionLimit) > 0) {
                throwTransactionLimitExceededException(request, transactionLimit);
            }
        } catch (CustomException e) {
            payTxnMasterTable.setErrMsg(e.getMessage());
            payTxnMasterTable.setErrCode(String.valueOf(e.getCode()));
            tptLimitAudit.setErrorMessage(e.getMessage());
            tptLimitAudit.setErrorCode(String.valueOf(e.getCode()));
            tptLimitAudit.setTptLimitResult(ResultEnum.FAILURE);
            throw e;
        }

        log.info("TPT and Transaction limit check passed for customerId: {}, referenceId: {}",
                request.getHashCustomerId(), referenceId);

    } catch (CustomException e) {
        payTxnMasterTable.setErrMsg(e.getMessage());
        payTxnMasterTable.setErrCode(String.valueOf(e.getCode()));
        throw e;
    } catch (RuntimeException e) {
        if (e.getCause() instanceof CustomException) {
            throw (CustomException) e.getCause();
        }
    } catch (Exception e) {
        log.error("Error during TPT/Transaction Limit check: {}", e.getMessage(), e);
        payTxnMasterTable.setErrMsg(e.getMessage());
        if (tptLimitAuditHolder[0] != null) {
            tptLimitAuditHolder[0].setErrorMessage(e.getMessage());
            tptLimitAuditHolder[0].setTptLimitResult(ResultEnum.FAILURE);
        }
        throw new CustomException(500, e.getMessage());
    } finally {
        CompletableFuture.runAsync(() -> {
            try {
                MDC.setContextMap(parentContextMap);
                tptLimitDAO.save(tptLimitAuditHolder[0]);
            } catch (Exception e) {
                log.error("Error while saving TptLimit Data, ErrorMessage {}", ExceptionUtils.getMessage(e));
            } finally {
                MDC.clear();
            }
        }, virtualThreadExecutor);
    }
    log.debug("Step 13: performTptAndTransactionLimitCheckAsync took {} ms", System.currentTimeMillis() - stepStart);
    return tptResult;
}
