private void performValidationAndExpectError(String fieldName, String fieldValue, ErrorCode errorCode) throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    ObjectNode requestBody = objectMapper.createObjectNode();
    requestBody.putPOJO(fieldName, fieldValue);

    MvcResult result = mockMvc.perform(post("/api/v1/debitcard/view")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody.toString()))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.type", is("error")))
            .andExpect(jsonPath("$.code", is(errorCode.name())))
            .andExpect(jsonPath("$.errorDescription").isString()) // Ensure errorDescription is a string
            .andReturn();

    // Extract the response JSON and validate errorCode and errorDescription
    String responseJson = result.getResponse().getContentAsString();
    JsonNode responseNode = objectMapper.readTree(responseJson);
    assertEquals(errorCode.name(), responseNode.get("code").asText());
    assertEquals(errorCode.getMessage(fieldName), responseNode.get("errorDescription").asText());
}
