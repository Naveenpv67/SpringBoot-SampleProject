import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/data")
public class DataController {

    private final AerospikeClient aerospikeClient; // Initialize this with your Aerospike client instance

    public DataController(AerospikeClient aerospikeClient) {
        this.aerospikeClient = aerospikeClient;
    }

    @PostMapping("/insert")
    public ResponseEntity<String> insertData(@RequestBody YourDataClass jsonData) {
        try {
            // Set your namespace, set name, and primary key
            String namespace = "your_namespace";
            String setName = "your_set_name";
            String primaryKey = jsonData.getPK(); // Replace with the actual method to get your primary key

            // Create a key using namespace, set name, and primary key
            Key key = new Key(namespace, setName, primaryKey);

            // Iterate through your JSON data and create bins dynamically
            jsonData.getData().forEach((key, value) -> {
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
