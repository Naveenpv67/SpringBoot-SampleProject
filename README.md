private String buildPaymentMessage(PaymentRequest request, PayTxnMasterTable payTxnMasterTable, 
                                   String status, String errorCode, String errorMessage, 
                                   MessageType messageType) {
    String maskedAccNo = MaskingUtil.maskDataWithBackVisible.apply(request.getPlainAccNo(), 4);
    String maskedTxnId = MaskingUtil.maskDataWithBackVisible.apply(payTxnMasterTable.getTransactionId(), 6);
    String maskedMerchant = MaskingUtil.maskDataWithBackVisible.apply(payTxnMasterTable.getMerchantName(), 4);
    
    switch (messageType) {
        case SMS:
            return buildSmsMessage(status, request.getTransactionAmount(), maskedAccNo, maskedMerchant, errorCode, errorMessage);
            
        case EMAIL:
            return buildEmailMessage(status, request.getTransactionAmount(), maskedAccNo, maskedTxnId, maskedMerchant, request.getReferenceId(), errorCode, errorMessage);
            
        default:
            return "Invalid message type";
    }
}

private String buildSmsMessage(String status, double amount, String maskedAccNo, 
                               String merchant, String errorCode, String errorMessage) {
    switch (status.toUpperCase()) {
        case "SUCCESS":
            return String.format("Payment Successful! Rs. %.2f from A/c %s to %s via HDFC Bank NetBanking. Not you? Call 18002586161.", 
                                 amount, maskedAccNo, merchant);

        case "FAILURE":
            return String.format("Payment of Rs. %.2f failed. A/c: %s, Merchant: %s, Error: %s %s", 
                                 amount, maskedAccNo, merchant, errorCode, errorMessage);

        case "REVERSAL":
            return String.format("Payment of Rs. %.2f has been reversed. Contact support if not initiated.", amount);

        default:
            return "Payment status update.";
    }
}

private String buildEmailMessage(String status, double amount, String maskedAccNo, 
                                 String maskedTxnId, String merchant, String referenceId, 
                                 String errorCode, String errorMessage) {
    if ("SUCCESS".equalsIgnoreCase(status)) {
        return String.format("Dear Customer,\n\nThank you for using HDFC Bank NetBanking! We confirm that your payment has been successfully processed.\n\n"
                             + "Transaction Details:\nAmount: Rs. %.2f\nAccount No: %s\nTransaction ID: %s\nMerchant Name: %s\nReference ID: %s\n"
                             + "Date & Time: %s\n\nNot You? If this wasn't your transaction, please contact us immediately at 1800-258-6161.", 
                             amount, maskedAccNo, maskedTxnId, merchant, referenceId, LocalDateTime.now());
    } else {
        return String.format("Dear Customer,\n\nYour payment of Rs. %.2f has failed.\n\n"
                             + "Transaction Details:\nAccount No: %s\nMerchant: %s\nReference ID: %s\n"
                             + "Error Code: %s\nError Message: %s\n\nPlease try again or contact support.", 
                             amount, maskedAccNo, merchant, referenceId, errorCode, errorMessage);
    }
}

private void sendPaymentAlert(PaymentRequest request, PayTxnMasterTable payTxnMasterTable, 
                              String status, String errorCode, String errorMessage) {
    String referenceId = request.getReferenceId();
    log.debug("Initiating payment alert process for referenceId: {}", referenceId);

    if (!notificationConfig.isEnabled()) {
        log.info("Payment notifications are disabled for referenceId: {}", referenceId);
        return;
    }

    String phoneNumber = request.getVarMobNo();
    String emailId = request.getVarEmlId();
    String maskedPhoneNumber = phoneNumber != null ? MaskingUtil.maskDataWithBackVisible.apply(phoneNumber, 4) : null;
    String maskedEmailId = emailId != null ? MaskingUtil.maskDataWithFrontVisible.apply(emailId, 4) : null;

    log.debug("Notification config: SMS enabled={}, Email enabled={}", notificationConfig.isSms(), notificationConfig.isEmail());
    log.debug("Recipient details: Masked Phone={}, Masked Email={}", maskedPhoneNumber, maskedEmailId);

    try {
        if (notificationConfig.isSms() && phoneNumber != null) {
            log.info("Sending SMS notification for referenceId: {} to: {}", referenceId, maskedPhoneNumber);
            String messageText = buildPaymentMessage(request, payTxnMasterTable, status, errorCode, errorMessage, MessageType.SMS);
            otpService.publishPaymentAlert(phoneNumber, MessageType.SMS, messageText, AlertType.ALERT, null, null);
            log.info("SMS notification sent successfully for referenceId: {}", referenceId);
        } else if (notificationConfig.isSms()) {
            log.warn("SMS notification enabled, but phone number is missing for referenceId: {}", referenceId);
        }

        if (notificationConfig.isEmail() && emailId != null) {
            log.info("Sending Email notification for referenceId: {} to: {}", referenceId, maskedEmailId);
            String messageText = buildPaymentMessage(request, payTxnMasterTable, status, errorCode, errorMessage, MessageType.EMAIL);
            otpService.publishPaymentAlert(emailId, MessageType.EMAIL, messageText, AlertType.ALERT, null, null);
            log.info("Email notification sent successfully for referenceId: {}", referenceId);
        } else if (notificationConfig.isEmail()) {
            log.warn("Email notification enabled, but email ID is missing for referenceId: {}", referenceId);
        }
    } catch (Exception e) {
        log.error("Error sending payment alert for referenceId: {}. Error: {}", referenceId, e.getMessage(), e);
    }
}
