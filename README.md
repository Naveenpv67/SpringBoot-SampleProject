import com.aerospike.client.AerospikeClient;
import com.aerospike.client.AerospikeException;
import com.aerospike.client.Key;
import com.aerospike.client.policy.ClientPolicy;
import com.aerospike.client.policy.Policy;
import com.aerospike.client.query.RecordSet;
import com.aerospike.client.query.Statement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AerospikeController {

    @GetMapping("/getSetsAndBins")
    public List<Map<String, Object>> getSetsAndBins(@RequestParam String namespace) {
        try {
            ClientPolicy clientPolicy = new ClientPolicy();
            AerospikeClient client = new AerospikeClient(clientPolicy, "10.216.34.32", 3000);

            List<Map<String, Object>> result = new ArrayList<>();

            // Set up a query statement to retrieve all records in the namespace
            Statement statement = new Statement();
            statement.setNamespace(namespace);

            // Configure the policy to include bin data
            Policy policy = new Policy();
            policy.includeBinData = true;

            // Execute the query and process the results
            RecordSet recordSet = client.query(policy, statement);
            while (recordSet.next()) {
                Map<String, Object> data = recordSet.getMap();
                result.add(data);
            }
            recordSet.close();

            client.close();

            return result;
        } catch (AerospikeException e) {
            e.printStackTrace(); // Handle the exception appropriately in your application
            return null;
        }
    }
}
