private PITxnStsInqResponseDTO parseTxnStsInqResponseBody(
        String transformedRequest, 
        PITxnStsInqRequestDTO request, 
        String piTxnStsInqResponseBody, 
        int statusCode) {

    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    PITxnStsInqResponseDTO piTxnReversalEntity = null;

    try {
        PiTxnStatusInquiryResponse response = 
                objectMapper.readValue(piTxnStsInqResponseBody, PiTxnStatusInquiryResponse.class);
        
        log.info(AMPSIssuerConstants.OBP_RESPONSE, piTxnStsInqResponseBody);

        // 1. Extract Status object
        Optional<Status> statusOpt = Optional.ofNullable(response.getStatus());

        // 2. Check if replyCode in Status is non-zero
        if (statusOpt.map(Status::getReplyCode).filter(code -> code != 0).isPresent()) {
            String errorCode = statusOpt.map(Status::getErrorCode).orElse("N/A");
            String errorMessage = statusOpt.map(Status::getReplyText).orElse("No Reply Text");
            return saveAsFailure(transformedRequest, request, piTxnStsInqResponseBody, 
                                 errorCode, errorMessage, statusCode, response);
        }

        // 3. Check for ResponseString and TransactionStatus
        Optional<TransactionStatus> transactionStatusOpt = Optional.ofNullable(response.getResponseString())
                .map(ResponseString::getTransactionStatus);

        // 4. Check if replyCode in TransactionStatus is non-zero
        if (transactionStatusOpt.map(TransactionStatus::getReplyCode).filter(code -> code != 0).isPresent()) {
            String errorCode = transactionStatusOpt.map(TransactionStatus::getErrorCode).orElse("N/A");
            String errorMessage = transactionStatusOpt.map(TransactionStatus::getReplyText).orElse("No Reply Text");
            return saveAsFailure(transformedRequest, request, piTxnStsInqResponseBody, 
                                 errorCode, errorMessage, statusCode, response);
        }

        // 5. Check for PI Transaction Status Inquiry DTO
        Optional<PiTxnStatusInquiryResDTO> piTxnStatusInquiryResDTOOpt = transactionStatusOpt
                .map(TransactionStatus::getPiTxnStatusInquiryResDTO);

        // 6. Evaluate Original Transaction Status
        if (piTxnStatusInquiryResDTOOpt.map(PiTxnStatusInquiryResDTO::getOrgTransactionStatus)
                                       .filter("Failure"::equalsIgnoreCase).isPresent()) {
            String errorCode = piTxnStatusInquiryResDTOOpt.map(PiTxnStatusInquiryResDTO::getOrgErrorCode).orElse("N/A");
            String errorMessage = piTxnStatusInquiryResDTOOpt.map(PiTxnStatusInquiryResDTO::getOrgErrorDescription)
                                                             .orElse("No Error Description");
            return saveAsFailure(transformedRequest, request, piTxnStsInqResponseBody, 
                                 errorCode, errorMessage, statusCode, response);
        }

        // 7. If all checks pass, mark as success
        piTxnReversalEntity = piTxnReversalEntityDAO.saveTxnReversalRequest(
                transformedRequest, request, piTxnStsInqResponseBody, 
                null, null, ResultEnum.SUCCESS, statusCode, response);

    } catch (Exception e) {
        log.error("Error while processing response of PI Txn reversal API Transaction");
        log.error("Error Details: {}", ExceptionUtils.getStackTrace(e));
    }

    return piTxnReversalEntity;
}



====
// 6. Evaluate Original Transaction Status with explicit success check
if (piTxnStatusInquiryResDTOOpt.isPresent()) {
    String orgTransactionStatus = piTxnStatusInquiryResDTOOpt
            .map(PiTxnStatusInquiryResDTO::getOrgTransactionStatus)
            .orElse("Unknown");

    if ("Failure".equalsIgnoreCase(orgTransactionStatus)) {
        String errorCode = piTxnStatusInquiryResDTOOpt.map(PiTxnStatusInquiryResDTO::getOrgErrorCode).orElse("N/A");
        String errorMessage = piTxnStatusInquiryResDTOOpt.map(PiTxnStatusInquiryResDTO::getOrgErrorDescription)
                                                         .orElse("No Error Description");
        return saveAsFailure(transformedRequest, request, piTxnStsInqResponseBody, 
                             errorCode, errorMessage, statusCode, response);
    } else if ("Success".equalsIgnoreCase(orgTransactionStatus)) {
        // Mark as success only if explicitly 'Success'
        return piTxnReversalEntityDAO.saveTxnReversalRequest(
                transformedRequest, request, piTxnStsInqResponseBody, 
                null, null, ResultEnum.SUCCESS, statusCode, response);
    } else {
        // Handle any other status (e.g., Pending, Unknown)
        String errorCode = "9999"; // Custom error code for unhandled status
        String errorMessage = "Unhandled Transaction Status: " + orgTransactionStatus;
        return saveAsFailure(transformedRequest, request, piTxnStsInqResponseBody, 
                             errorCode, errorMessage, statusCode, response);
    }
}
