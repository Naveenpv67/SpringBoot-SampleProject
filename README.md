// Inside the importDataIntoAerospike method
private void importDataIntoAerospike(JsonNode jsonNode, AerospikeClient aerospikeClient, String setName) {
    if (jsonNode.isArray()) {
        // If the JSON represents an array of objects
        ArrayNode arrayNode = (ArrayNode) jsonNode;
        for (JsonNode objectNode : arrayNode) {
            handleJsonObject(objectNode, aerospikeClient, setName);
        }
    } else if (jsonNode.isObject()) {
        // If the JSON represents a single object
        handleJsonObject(jsonNode, aerospikeClient, setName);
    } else {
        throw new IllegalArgumentException("Invalid JSON structure");
    }
}

private void handleJsonObject(JsonNode objectNode, AerospikeClient aerospikeClient, String setName) {
    // Extract the unique identifier field
    String id = extractId(objectNode);

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

private String extractId(JsonNode objectNode) {
    // Assuming each object has a unique identifier field named "PK"
    JsonNode idNode = objectNode.get("PK");
    if (idNode != null && idNode.isTextual()) {
        return idNode.asText();
    }

    // If "PK" is not found at the top level, you may need to traverse nested structures
    // Adjust this part based on your JSON structure
    // For example, if the unique identifier is inside a nested object "nestedObject" -> "PK"
    JsonNode nestedIdNode = objectNode.path("nestedObject").path("PK");
    if (nestedIdNode != null && nestedIdNode.isTextual()) {
        return nestedIdNode.asText();
    }

    // If the unique identifier is not found, handle accordingly
    throw new IllegalArgumentException("Unique identifier 'PK' not found in the JSON structure");
}
