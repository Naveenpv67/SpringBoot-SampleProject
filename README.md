 if (!responseBody.isEmpty()) {
            try {
                ResponseDTO<?> responseDTO = mapper.readValue(responseBody, ResponseDTO.class);
                transactionLoggingUtil.logFailedTransaction(requestBody, responseBody,
                        Optional.ofNullable(responseDTO.getErrorCode()).map(Object::toString).orElse("UNKNOWN_ERROR"),
                        Optional.ofNullable(responseDTO.getMessage()).orElse("Unknown error message"),
                        transactionLoggingRequest);
            } catch (MismatchedInputException e) {
                log.error("Failed to parse response body: {}", responseBody, e);
                transactionLoggingUtil.logFailedTransaction(requestBody, responseBody,
                        "PARSE_ERROR", "Failed to parse response body", transactionLoggingRequest);
            }
        } else {
            log.warn("Response body is empty for failed transaction");
            transactionLoggingUtil.logFailedTransaction(requestBody, responseBody,
                    "EMPTY_RESPONSE", "Response body is empty", transactionLoggingRequest);
        }
    }
