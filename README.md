import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/data")
public class DataController {

    private final AerospikeClient aerospikeClient; // Initialize this with your Aerospike client instance

    private final ObjectMapper objectMapper;

    public DataController(AerospikeClient aerospikeClient, ObjectMapper objectMapper) {
        this.aerospikeClient = aerospikeClient;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/insert")
    public ResponseEntity<String> insertData(@RequestBody String jsonData) {
        try {
            // Convert the JSON string to a Map
            @SuppressWarnings("unchecked")
            Map<String, Object> dataMap = objectMapper.readValue(jsonData, Map.class);

            // Set your namespace, set name, and primary key
            String namespace = "your_namespace";
            String setName = "your_set_name";
            String primaryKey = (String) dataMap.get("PK"); // Replace with the actual key in your data

            // Create a key using namespace, set name, and primary key
            Key key = new Key(namespace, setName, primaryKey);

            // Iterate through the data map and create bins dynamically
            dataMap.forEach((key, value) -> {
                Bin bin = new Bin(key, value);
                aerospikeClient.put(null, key, bin);
            });

            return new ResponseEntity<>("Data inserted successfully", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to insert data", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
