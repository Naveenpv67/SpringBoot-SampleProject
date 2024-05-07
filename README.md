import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {
        // Instantiate your model class
        YourModelClass model = new YourModelClass();

        // Convert the model class to HashMap using ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> map = objectMapper.convertValue(model, Map.class);

        // Iterate over the HashMap to check and replace null values
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() == null) {
                // Replace null value with null string encoded with double quotations
                entry.setValue("\"null\"");
            }
        }

        // Display the modified HashMap
        System.out.println(map);
    }
}
