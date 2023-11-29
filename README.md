import com.fasterxml.jackson.databind.JsonNode;

public class JsonNodeWithFileName {
    private String filename;
    private JsonNode jsonNode;

    public JsonNodeWithFileName(String filename, JsonNode jsonNode) {
        this.filename = removeJsonExtension(filename);
        this.jsonNode = jsonNode;
    }

    public String getFilename() {
        return filename;
    }

    public JsonNode getJsonNode() {
        return jsonNode;
    }

    private String removeJsonExtension(String filename) {
        if (filename.endsWith(".json")) {
            return filename.substring(0, filename.length() - 5);
        }
        return filename;
    }
}
