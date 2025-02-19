private PITxnStsInqResponseDTO parseTxnStsInqResponseBody(String transformedRequest, 
                                                          PITxnStsInqRequestDTO request, 
                                                          String piTxnStsInqResponseBody, 
                                                          int statusCode) {

    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    try {
        PiTxnStatusInquiryResponse response = objectMapper.readValue(
                piTxnStsInqResponseBody, PiTxnStatusInquiryResponse.class);
        log.info(AMPSIssuerConstants.OBP_RESPONSE, piTxnStsInqResponseBody);

        // 1. Check the Status object
        Optional<Status> statusOpt = Optional.ofNullable(response.getStatus());
        int replyCode = statusOpt.map(Status::getReplyCode).orElse(0);

        if (replyCode != 0) {
            String replyText = statusOpt.map(Status::getReplyText)
                    .filter(text -> !text.isEmpty())
                    .orElse("Payment processing failed due to an unknown error.");
            String errorCode = statusOpt.map(Status::getErrorCode).orElse("UNKNOWN_ERROR");

            return buildFailureResponse("Failure", errorCode, replyText, response);
        }

        // 2. Check the ResponseString and TransactionStatus objects
        Optional<TransactionStatus> transactionStatusOpt = Optional.ofNullable(response.getResponseString())
                .map(ResponseString::getTransactionStatus);
        
        int txnReplyCode = transactionStatusOpt.map(TransactionStatus::getReplyCode).orElse(0);
        
        if (txnReplyCode != 0) {
            String txnReplyText = transactionStatusOpt.map(TransactionStatus::getReplyText)
                    .filter(text -> !text.isEmpty())
                    .orElse("Transaction failed due to an unspecified reason.");
            
            String txnErrorCode = transactionStatusOpt.map(TransactionStatus::getErrorCode)
                    .orElse("UNKNOWN_TXN_ERROR");

            return buildFailureResponse("Failure", txnErrorCode, txnReplyText, response);
        }

        // 3. Check the Originator Transaction Status in PITransactionStatusEnquiryResultDTO
        Optional<PITransactionStatusEnquiryResultDTO> piResultOpt = 
                Optional.ofNullable(response.getResponseString())
                        .map(ResponseString::getTransactionStatus)
                        .map(TransactionStatus::getPiTransactionStatusEnquiryResultDTO);

        String orgTransactionStatus = piResultOpt.map(PITransactionStatusEnquiryResultDTO::getOrgTransactionStatus)
                .orElse("UNKNOWN");

        boolean isOriginatorFailed = "FAILURE".equalsIgnoreCase(orgTransactionStatus);

        if (isOriginatorFailed) {
            String orgErrorCode = piResultOpt.map(PITransactionStatusEnquiryResultDTO::getOrgErrorCode)
                    .orElse("UNKNOWN_ORG_ERROR");
            
            String orgErrorDescription = piResultOpt.map(PITransactionStatusEnquiryResultDTO::getOrgErrorDescription)
                    .filter(desc -> !desc.isEmpty())
                    .orElse("Original transaction failed due to an unspecified error.");

            return buildFailureResponse("Failure", orgErrorCode, orgErrorDescription, response);
        }

        // Success Scenario
        return buildSuccessResponse(orgTransactionStatus, response);

    } catch (Exception e) {
        log.error("Error while processing response of PI Txn reversal API Transaction");
        log.error("Error Details: {}", ExceptionUtils.getStackTrace(e));
    }

    return buildFailureResponse("Failure", "PARSING_ERROR", 
            "Failed to parse payment transaction response.", null);
}

===

private PITxnStsInqResponseDTO buildFailureResponse(String status, String errorCode, String errorDescription, PiTxnStatusInquiryResponse response) {
    PITxnStsInqResponseDTO dto = new PITxnStsInqResponseDTO();
    dto.setOrgTransactionStatus(status);
    dto.setOrgErrorCode(errorCode);
    dto.setOrgErrorDescription(errorDescription);
    dto.setRefUserNo(response != null ? response.getRefUserNo() : "N/A");
    dto.setOrgDatRequest(response != null ? response.getOrgDatRequest() : "N/A");
    return dto;
}

private PITxnStsInqResponseDTO buildSuccessResponse(String status, PiTxnStatusInquiryResponse response) {
    PITxnStsInqResponseDTO dto = new PITxnStsInqResponseDTO();
    dto.setOrgTransactionStatus(status);
    dto.setOrgErrorCode(""); // No error
    dto.setOrgErrorDescription(""); // No error message
    dto.setRefUserNo(response != null ? response.getRefUserNo() : "N/A");
    dto.setOrgDatRequest(response != null ? response.getOrgDatRequest() : "N/A");
    return dto;
}
