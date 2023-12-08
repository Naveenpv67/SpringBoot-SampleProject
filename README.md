// Inside the importDataIntoAerospike method
private void importDataIntoAerospike(JsonNode jsonNode, AerospikeClient aerospikeClient, String setName) {
    if (jsonNode.isArray()) {
        // If the JSON represents an array of objects
        ArrayNode arrayNode = (ArrayNode) jsonNode;
        for (JsonNode objectNode : arrayNode) {
            handleJsonObject(objectNode, aerospikeClient, setName);
        }
    } else {
        throw new IllegalArgumentException("Invalid JSON structure");
    }
}

private void handleJsonObject(JsonNode objectNode, AerospikeClient aerospikeClient, String setName) {
    // Extract the unique identifier field "PK"
    String id = objectNode.get("PK").asText();

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
