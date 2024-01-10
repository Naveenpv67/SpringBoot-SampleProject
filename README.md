private void logResponseDetails(ServerHttpResponse response) {
    logger.info("Response status code: {}", response.getStatusCode());
    logger.info("Response headers: {}", response.getHeaders());

    // Log response body (if present)
    response.writeWith(response.getBody().map(buffer -> {
        byte[] content = new byte[buffer.readableByteCount()];
        buffer.read(content);
        DataBufferUtils.release(buffer);
        String responseBody = new String(content, StandardCharsets.UTF_8);

        // Parse JSON array and filter items
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<Map<String, Object>> items = objectMapper.readValue(responseBody, new TypeReference<List<Map<String, Object>>>() {});
            
            // Filter and log only two items (adjust the condition as needed)
            List<Map<String, Object>> filteredItems = items.stream()
                    .filter(item -> (int) item.get("id") % 2 == 0) // Example condition: even id
                    .limit(2)
                    .collect(Collectors.toList());

            logger.info("Filtered Response body: {}", filteredItems);
            return response.bufferFactory().wrap(objectMapper.writeValueAsBytes(filteredItems));
        } catch (IOException e) {
            logger.error("Error parsing JSON array", e);
            return response.bufferFactory().wrap(content);
        }
    }));
}
