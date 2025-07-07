import java.util.concurrent.*;
import java.util.*;
import org.slf4j.MDC;

public class DummyFetchTxnHandler {

    // ... (Dummy class definitions from previous answer)

    @Override
    public RespFetchTxn handleRequestFecthTxnDetailsV1(ReqFetchTxnDetails request, String sematicKey)
            throws CustomException {
        String referenceId = null;
        RespFetchTxn respFetchTxn = new RespFetchTxn();
        PayTxnMasterTable payTxnMasterTable = null;
        String expiry = String.valueOf(nbblConfig.getTxnExpiry());
        String txnId = CommonUtils.getTxnId(yamlConfig.getOrgId());
        IssuerFetchResponseTable resp = null;

        long overallStart = System.currentTimeMillis();

        try (ExecutorService virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor()) {
            Map<String, String> parentContextMap = MDC.getCopyOfContextMap();

            // Step 1: Initial logging and setup
            long stepStart = System.currentTimeMillis();
            respFetchTxn.setTs(CommonUtils.getTs());
            respFetchTxn.setTxnID(txnId);
            log.info("Step 1: Initial setup took {} ms", System.currentTimeMillis() - stepStart);

            // Step 2: Extracting data from URL
            stepStart = System.currentTimeMillis();
            CompletableFuture<String> rIdResult = CompletableFuture.supplyAsync(() -> getDataFromUrl(request.getInitiationModeData(), "rId=([^&]+)"), virtualThreadExecutor);
            CompletableFuture<String> initiationModeResult = CompletableFuture.supplyAsync(() -> getDataFromUrl(request.getInitiationModeData(), "mode=([^&]+)"), virtualThreadExecutor);
            CompletableFuture<String> ttsRegexResult = CompletableFuture.supplyAsync(() -> getDataFromUrl(request.getInitiationModeData(), "Tts=([^&]+)"), virtualThreadExecutor);
            CompletableFuture<String> expiryResult = CompletableFuture.supplyAsync(() -> getDataFromUrl(request.getInitiationModeData(), "expiry=([^&]+)"), virtualThreadExecutor);
            referenceId = rIdResult.get();
            request.setInitMode(initiationModeResult.get());
            respFetchTxn.setRefID(referenceId);
            log.info("Step 2: Extracting data from URL took {} ms", System.currentTimeMillis() - stepStart);

            // Step 3: Validate Reference ID
            stepStart = System.currentTimeMillis();
            if (org.springframework.util.StringUtils.hasText(request.getReferenceId())
                    && !referenceId.equalsIgnoreCase(request.getReferenceId())) {
                log.error("Reference ID mismatch. ReferenceId in payload {}, and in headers {}",
                        ESAPI.encoder().encodeForHTML(referenceId),
                        ESAPI.encoder().encodeForHTML(request.getReferenceId()));
                throw new ClientValidationException(ResponseEnum.REF_ID_MISMATCH.getCode(),
                        ResponseEnum.REF_ID_MISMATCH.getMessage());
            }
            request.setReferenceId(referenceId);
            log.info("Step 3: Validate Reference ID took {} ms", System.currentTimeMillis() - stepStart);

            // Step 4: Get plain customer ID from hash
            stepStart = System.currentTimeMillis();
            CompletableFuture<String> futureResult = CompletableFuture.supplyAsync(() -> {
                try {
                    MDC.setContextMap(parentContextMap);
                    return getPlainCustIdFromHashCustId(request.getCustomerId());
                } finally {
                    MDC.clear();
                }
            }, virtualThreadExecutor);
            String ttsRegex = ttsRegexResult.get();
            expiry = expiryResult.get();
            log.info("Step 4: Get plain customer ID and extract tts/expiry took {} ms", System.currentTimeMillis() - stepStart);

            // Step 5: Validate customer and reference ID existence
            stepStart = System.currentTimeMillis();
            CompletableFuture<Void> clientIdValidationFuture = CompletableFuture.runAsync(() -> {
                try {
                    MDC.setContextMap(parentContextMap);
                    customerDetailsDao.validateCustIdExists(request.getCustomerId());
                } finally {
                    MDC.clear();
                }
            }, virtualThreadExecutor);
            CompletableFuture<Boolean> refIdCheckResult = CompletableFuture.supplyAsync(() -> {
                try {
                    MDC.setContextMap(parentContextMap);
                    return requestFetchTransactionDAO.checkRefIdExist(request.getReferenceId());
                } finally {
                    MDC.clear();
                }
            }, virtualThreadExecutor);
            clientIdValidationFuture.get();
            if (refIdCheckResult.get()) {
                log.warn("ReqFetch-5.1 Duplicate referenceId found:{}", ESAPI.encoder().encodeForHTML(referenceId));
                throw new ClientValidationException(ResponseEnum.REF_ID_EXISTS.getCode(),
                        ResponseEnum.REF_ID_EXISTS.getMessage());
            }
            log.info("Step 5: Validate customer/refId existence took {} ms", System.currentTimeMillis() - stepStart);

            // Step 6: Rate limit check
            stepStart = System.currentTimeMillis();
            CompletableFuture<Void> rateLimitCheck = CompletableFuture.runAsync(() -> {
                try {
                    MDC.setContextMap(parentContextMap);
                    rateLimitDAO.cacheRateLimitDetails(request.getCustomerId());
                } finally {
                    MDC.clear();
                }
            }, virtualThreadExecutor);
            rateLimitCheck.get();
            log.info("Step 6: Rate limit check took {} ms", System.currentTimeMillis() - stepStart);

            // Step 7: Create PayTxnMasterTable
            stepStart = System.currentTimeMillis();
            payTxnMasterTable = createPayTxnMasterTable(request, referenceId, request.getCustomerId(), expiry, txnId);
            log.info("Step 7: Create PayTxnMasterTable took {} ms", System.currentTimeMillis() - stepStart);

            // Step 8: Insert client entity
            stepStart = System.currentTimeMillis();
            String expiry1 = expiry;
            CompletableFuture<IssuerFetchRequestTable> clientEntityResult = CompletableFuture.supplyAsync(() -> {
                try {
                    MDC.setContextMap(parentContextMap);
                    return requestFetchTransactionDAO.insertClientDataIssuerTable(request, request.getReferenceId(),
                            expiry1, request.getCustomerId());
                } finally {
                    MDC.clear();
                }
            }, virtualThreadExecutor);
            log.info("Step 8: Insert client entity took {} ms", System.currentTimeMillis() - stepStart);

            // Step 9: Prepare payload for NBBL
            stepStart = System.currentTimeMillis();
            CompletableFuture<org.apache.commons.lang3.tuple.ImmutablePair<ReqTxnFetchDetails, NBBLPayload>> payloadResult =
                    CompletableFuture.supplyAsync(() -> {
                        try {
                            MDC.setContextMap(parentContextMap);
                            ReqTxnFetchDetails nbblRequest = transformerUtil.tranformNbblRequest(request, request.getReferenceId(), ttsRegex);
                            NBBLPayload payload = null;
                            try {
                                String nbblRequestJson = objectMapper.writeValueAsString(nbblRequest);
                                payload = nbblGeneratePayLoadService.getPayload(nbblRequestJson,
                                        yamlConfig.getAmpsPrivateKey(), yamlConfig.getNbblpublicKey());
                            } catch (Exception e) {
                                log.error("Error -> ", e);
                            }
                            return org.apache.commons.lang3.tuple.ImmutablePair.of(nbblRequest, payload);
                        } finally {
                            MDC.clear();
                        }
                    }, virtualThreadExecutor);
            ReqTxnFetchDetails nbblRequest = payloadResult.get().getLeft();
            NBBLPayload payload = payloadResult.get().getRight();
            log.info("Step 9: Prepare NBBL payload took {} ms", System.currentTimeMillis() - stepStart);

            // Step 10: Insert NBBL data
            stepStart = System.currentTimeMillis();
            IssuerFetchRequestTable clientNbblEntity = requestFetchTransactionDAO
                    .insertNbblDataIssuerTable(clientEntityResult.get(), nbblRequest, payload, request);
            log.info("Step 10: Insert NBBL data took {} ms", System.currentTimeMillis() - stepStart);

            // Step 11: Call NBBL downstream
            stepStart = System.currentTimeMillis();
            CompletableFuture<ReqFetchTxnAckResponse> callNBBlRes = CompletableFuture.supplyAsync(() -> {
                try {
                    MDC.setContextMap(parentContextMap);
                    return nbblDownstreamCaller.callNBBLReqFetchTxnDetailsV1(payload, request.getReferenceId(), clientNbblEntity);
                } finally {
                    MDC.clear();
                }
            }, virtualThreadExecutor);
            ReqFetchTxnAckResponse response = callNBBlRes.get();
            log.info("Step 11: Call NBBL downstream took {} ms", System.currentTimeMillis() - stepStart);

            // Step 12: Call TPT/Account details
            stepStart = System.currentTimeMillis();
            String plainCustIdRes = futureResult.get();
            CompletableFuture<org.apache.commons.lang3.tuple.ImmutablePair<String, Map<String, Object>>> futureTPTResult =
                    CompletableFuture.supplyAsync(() -> {
                        try {
                            MDC.setContextMap(parentContextMap);
                            return obpDownstreamCaller.callLimitService(CommonUtils.getRefId(nbblConfig.getBankcode()), yamlConfig.getCsChannelId(), plainCustIdRes);
                        } finally {
                            MDC.clear();
                        }
                    }, virtualThreadExecutor);
            CompletableFuture<GetAccountDetailsResponse> futureAccResult = CompletableFuture.supplyAsync(() -> {
                try {
                    MDC.setContextMap(parentContextMap);
                    GetAccountDetailsRequest req = new GetAccountDetailsRequest(plainCustIdRes, request.getReferenceId());
                    return getAccountDetails(req);
                } finally {
                    MDC.clear();
                }
            }, virtualThreadExecutor);
            log.info("Step 12: Setup TPT/account detail calls took {} ms", System.currentTimeMillis() - stepStart);

            // Step 13: Insert NBBL response
            stepStart = System.currentTimeMillis();
            CompletableFuture.runAsync(() -> {
                try {
                    MDC.setContextMap(parentContextMap);
                    requestFetchTransactionDAO.insertNbblResponseDataIssuerTable(clientNbblEntity, response);
                } finally {
                    MDC.clear();
                }
            }, virtualThreadExecutor);
            log.info("Step 13: Insert NBBL response took {} ms", System.currentTimeMillis() - stepStart);

            // Step 14: Check NBBL response
            stepStart = System.currentTimeMillis();
            if (response.getResult().equals(ResultEnum.FAILURE.name())) {
                log.error("Error returned from NBBL during reqfetch for refId {}. Error is {}", ESAPI.encoder().encodeForHTML(referenceId), ESAPI.encoder().encodeForHTML(response.getErrorDetails().toString()));
                payTxnMasterTable.setDevErrCode(response.getErrorDetails().iterator().next().getErrorCd());
                payTxnMasterTable.setDevErrMsg(response.getErrorDetails().iterator().next().getErrorDtl());
                throw new CustomException(4210, "Something went wrong. Please try again");
            }
            log.info("Step 14: Check NBBL response took {} ms", System.currentTimeMillis() - stepStart);

            // Step 15: Poll for NBBL ack
            stepStart = System.currentTimeMillis();
            long startTime = System.currentTimeMillis();
            boolean isNBBLResponseReturns = false;
            boolean await = false;
            DirectMessageReceiver directMessageReceiver = solaceService.getDirectMessageReceiver(
                    solaceConfig.getTopicPrefix() + AMPSIssuerConstants.NBBL_RESPONSE_TOPIC + referenceId);
            if (null != directMessageReceiver) {
                CountDownLatch countDownLatch = new CountDownLatch(1);
                directMessageReceiver.receiveAsync(getMessageHandler(referenceId, countDownLatch));
                await = countDownLatch.await(solaceConfig.getConsumerTimeOut(), TimeUnit.MILLISECONDS);
                directMessageReceiver.terminate(solaceConfig.getMessageReceiverTimeOut());
            }
            if (!await) {
                while (!isNBBLResponseReturns) {
                    log.info("ReqFetch-8 Polling NBBL for RespFetchTxnDetails.");
                    isNBBLResponseReturns = requestFetchTransactionDAO
                            .checkRefIdWithStatusExist(nbblRequest.getTxn().getRefID());
                    if (!isNBBLResponseReturns) {
                        try { Thread.sleep(200); } catch (InterruptedException e) { log.error("Interrupted", e); }
                    }
                    if (System.currentTimeMillis() - startTime > yamlConfig.getIssuerTimeout()) {
                        payTxnMasterTable.setErrCode("503");
                        payTxnMasterTable.setErrMsg("RespFetchTxnDetails not found for the reference Id");
                        payTxnMasterTable.setAmpsTxnResult(ResultEnum.FAILURE);
                        return buildErrorResponseForReqFetch(respFetchTxn, request, "4207", "Transaction Timed -out / Session Timeout", txnId);
                    }
                }
            }
            log.info("Step 15: Poll/Wait for NBBL ack took {} ms", System.currentTimeMillis() - stepStart);

            // Step 16: DB & Account updates
            stepStart = System.currentTimeMillis();
            resp = requestFetchTransactionDAO.getRespCheckTxnDetails(referenceId);
            request.setCustomerId(plainCustIdRes);
            updatePayTxnMasterWithNbblResponse(request, payTxnMasterTable, resp, clientNbblEntity);
            PaymentRequest paymentRequest = createPaymentRequest(request, request.getCustomerId(), resp, plainCustIdRes);
            org.apache.commons.lang3.tuple.ImmutablePair<String, Map<String, Object>> tptResult = futureTPTResult.get();
            PayTxnMasterTable payTxnMasterTable1 = payTxnMasterTable;
            CompletableFuture<ResultEnum> tptLimitRes = CompletableFuture.supplyAsync(() -> {
                try {
                    MDC.setContextMap(parentContextMap);
                    return performTptAndTransactionLimitCheckL1(tptResult, payTxnMasterTable1, paymentRequest, AMPSIssuerConstants.REQFTCH);
                } finally {
                    MDC.clear();
                }
            }, virtualThreadExecutor);
            GetAccountDetailsResponse accountDetails = futureAccResult.get();
            filterAccounts(accountDetails, resp.getRespAmountValue());
            if (org.springframework.util.ObjectUtils.isEmpty(accountDetails.getAccountDetails())) {
                throw new CustomException(42011, "Incorrect Account Mismatch in payment details");
            }
            cipherAccountDetails(accountDetails, yamlConfig.getHashSecKey(), request.getSessionKey(), request.getClientId(), sematicKey);
            CompletableFuture.runAsync(() -> {
                try {
                    MDC.setContextMap(parentContextMap);
                    accountDetailsDAO.cacheAccountDetails(accountDetails.getAccountDetails(), request.getCustomerId(), request.getClientId(), request.getSessionKey(), request.getReferenceId(), request.getClientChannel());
                } finally {
                    MDC.clear();
                }
            }, virtualThreadExecutor);
            tptLimitRes.get();
            respFetchTxn.setGetAccountDetailsResponse(accountDetails);
            log.info("Step 16: DB/account updates took {} ms", System.currentTimeMillis() - stepStart);

            log.info("ReqFetch-1 handleRequestFecthTxnDetailsV1 Exit with timestamp: {}", System.currentTimeMillis());
            log.info("Overall method execution took {} ms", System.currentTimeMillis() - overallStart);
            return transformerUtil.buildResFetchTxnDetails(resp, clientNbblEntity, respFetchTxn);

        } // ... rest of catch/finally blocks stay the same as before
        // ... (Copy unchanged catch/finally blocks from previous answer)
    }

    // ... (Rest of dummy class as before)
}
