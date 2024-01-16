public String sampleFunc(String outData) {
    System.out.println(outData);

    // Assuming outData is a JSON string
    ObjectMapper objectMapper = new ObjectMapper();
    try {
        // Parse the JSON response
        JsonNode jsonNode = objectMapper.readTree(outData);

        // Extract the list of debit cards
        JsonNode debitCardsNode = jsonNode.path("body").path("debit_cards");

        // Filter debit cards based on the primary_account condition
        List<JsonNode> filteredDebitCards = new ArrayList<>();
        for (JsonNode debitCardNode : debitCardsNode) {
            JsonNode primaryAccountNode = debitCardNode.path("primary_account");
            if (primaryAccountNode.isTextual() && primaryAccountNode.textValue().startsWith("501001146419")) {
                filteredDebitCards.add(debitCardNode);
            }
        }

        // Update the original JSON response with the filtered debit cards
        ((ObjectNode) jsonNode.path("body")).set("debit_cards", objectMapper.valueToTree(filteredDebitCards));

        // Convert the modified JSON back to a string
        return objectMapper.writeValueAsString(jsonNode);

    } catch (JsonProcessingException e) {
        e.printStackTrace();
        // Handle the exception as needed
        return outData;
    }
}
