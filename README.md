private boolean isSuccessfulResponse(ResponseEntity<String> response) {
    if (!response.getStatusCode().is2xxSuccessful()) {
        return false; // If status code is not 2xx, treat it as failure
    }
    
    try {
        Map<String, Object> responseMap = objectMapper.readValue(response.getBody(), new TypeReference<Map<String, Object>>() {});
        String result = (String) responseMap.get("result");
        String errorCode = (String) responseMap.get("errCode");

        if ("SUCCESS".equalsIgnoreCase(result)) {
            return true; // Success case
        } else if ("FAILURE".equalsIgnoreCase(result)) {
            return errorCode != null && !errorCode.startsWith("AMPS"); 
            // If errorCode starts with AMPS, return false (FAILURE), else return true (SUCCESS)
        }
    } catch (Exception e) {
        log.error("Error while parsing response JSON: {}", e.getMessage());
    }
    return false; // Default failure case
}
