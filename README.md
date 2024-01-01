import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aerospike.client.Record;
import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.aerospike.client.policy.ClientPolicy;

@RestController
@RequestMapping("/api")
public class DataController {

    @Autowired
    private AerospikeClient aerospikeClient; // Make sure you have this bean configured in your application

    @GetMapping("/fetchData/{set}/{primaryKey}")
    public String fetchData(@PathVariable String set, @PathVariable String primaryKey) {
        Key key = new Key("namespace", set, primaryKey);
        Record record = aerospikeClient.get(null, key);

        if (record != null) {
            // Assuming 'data' is the name of the bin you want to fetch
            Object data = record.getValue("data");
            return "Data retrieved: " + data.toString();
        } else {
            return "No data found for the given primary key.";
        }
    }
}
