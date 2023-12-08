// Inside the importDataIntoAerospike method
private void importDataIntoAerospike(JsonNode jsonNode, AerospikeClient aerospikeClient, String setName) {
    if (jsonNode.isArray() && jsonNode.size() > 0) {
        // Extracting the first array within the outer array
        ArrayNode firstArray = (ArrayNode) jsonNode.get(0);
        
        for (JsonNode objectNode : firstArray) {
            handleJsonObject(objectNode, aerospikeClient, setName);
        }
    } else {
        throw new IllegalArgumentException("Invalid or empty JSON array structure");
    }
}

private void handleJsonObject(JsonNode objectNode, AerospikeClient aerospikeClient, String setName) {
    // Extract the unique identifier field "PK"
    JsonNode idNode = objectNode.get("PK");
    if (idNode == null || !idNode.isTextual()) {
        throw new IllegalArgumentException("Invalid or missing 'PK' field in the JSON structure");
    }

    String id = idNode.asText();

    // Create Aerospike key
    Key key = new Key("test", setName, id);

    // Create Aerospike bins from JSON fields
    objectNode.fields().forEachRemaining(entry -> {
        String fieldName = entry.getKey();
        String fieldValue = entry.getValue().asText();
        Bin bin = new Bin(fieldName, fieldValue);
        aerospikeClient.put(null, key, bin);
    });
}
