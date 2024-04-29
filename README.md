import java.util.HashMap;
import java.util.Map;

public class LogController {

    // Autowire your TransactionLogProperties bean
    private final TransactionLogProperties transactionLogProperties;

    // Constructor
    public LogController(TransactionLogProperties transactionLogProperties) {
        this.transactionLogProperties = transactionLogProperties;
    }

    @PostMapping("/log")
    public String logRequest(@RequestBody RequestDTO requestDTO) {
        // Initialize the request object using tmiMap
        Map<String, String> tmiMap = new HashMap<>(transactionLogProperties.getMasterInfoMap());

        // Create a copy of the RequestDTO
        Map<String, String> requestCopy = new HashMap<>(requestDTO);

        // Load alias names from application.properties
        Map<String, String> aliasMap = transactionLogProperties.getAliasNameMap();

        // Replace alias keys with original keys in the RequestDTO copy
        for (Map.Entry<String, String> entry : aliasMap.entrySet()) {
            String aliasKey = entry.getKey();
            String originalKey = entry.getValue();
            if (requestCopy.containsKey(aliasKey)) {
                // Get the value associated with the alias key
                String value = requestCopy.get(aliasKey);
                // Remove the alias key from the RequestDTO copy
                requestCopy.remove(aliasKey);
                // Add the original key with the same value
                requestCopy.put(originalKey, value);
            }
        }

        // Map values to tmiMap based on the keys present in the RequestDTO copy
        for (Map.Entry<String, String> entry : requestCopy.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (tmiMap.containsKey(key)) {
                tmiMap.put(key, value);
            }
        }

        // Now you have the RequestDTO copy with alias keys replaced by original keys
        // and the tmiMap populated with values from the RequestDTO copy
        // Proceed with logging or further processing

        return "Logging completed";
    }
}
