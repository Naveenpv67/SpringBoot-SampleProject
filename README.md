import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class JsonArrayProcessor {
    public static void main(String[] args) {
        // Your array of JSON objects as a string
        String jsonArrayString = "[{\"name\":\"John\",\"age\":30,\"city\":\"New York\"},{\"name\":\"Alice\",\"age\":25,\"city\":\"London\"}]";

        // Specify the field name you want to extract
        String fieldName = "name";

        // Parse JSON array using Jackson
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonArray = objectMapper.readTree(jsonArrayString);

            // List to store extracted values
            List<String> fieldValues = new ArrayList<>();

            // Iterate through each JSON object in the array
            for (JsonNode jsonObject : jsonArray) {
                // Extract the value of the specified field
                if (jsonObject.has(fieldName)) {
                    String fieldValue = jsonObject.get(fieldName).asText();
                    fieldValues.add(fieldValue);
                }
            }

            // Print the list of extracted values
            System.out.println("Extracted Values: " + fieldValues);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
