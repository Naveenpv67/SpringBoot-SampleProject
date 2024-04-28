import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
public class WasteController {

    @Autowired
    private TransactionLogProperties transactionLogProperties;

    @PostMapping("/log")
    public void logRequest(@RequestBody RequestDTO requestDTO) {
        // Initialize the TMIMap with default or null values
        Map<String, Object> TMIMap = new HashMap<>();
        for (String key : transactionLogProperties.getMasterInfoMap().keySet()) {
            TMIMap.put(key, null); // Initialize all values to null
        }

        // Map all the values from the RequestDTO object to TMIMap
        for (Map.Entry<String, String> entry : requestDTO.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            // Check if alias name exists in the properties file
            if (transactionLogProperties.getAliasNameMap().containsKey(key)) {
                String alias = transactionLogProperties.getAliasNameMap().get(key);
                TMIMap.put(alias, value);
            } else {
                TMIMap.put(key, value);
            }
        }

        // Log the mapped values
        logToLibrary(TMIMap);
    }

    // Method to log values to the library
    private void logToLibrary(Map<String, Object> mappedValues) {
        // Logic to log values using the library jar
    }
}
