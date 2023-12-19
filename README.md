import com.aerospike.client.AerospikeClient;
import com.aerospike.client.AerospikeException;
import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.aerospike.client.Record;
import com.aerospike.client.policy.ClientPolicy;
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

    @GetMapping("/getAllSetData")
    public List<Map<String, Object>> getAllSetData(@RequestParam String namespace) {
        try {
            ClientPolicy clientPolicy = new ClientPolicy();
            AerospikeClient client = new AerospikeClient(clientPolicy, "10.216.34.32", 3000);

            List<Map<String, Object>> result = new ArrayList<>();

            // Fetch the set names for the given namespace using Info.request
            String response = Info.request(client.getNodes()[0], "sets");
            List<String> setNames = Arrays.stream(response.split(";"))
                    .map(setInfo -> setInfo.split(":")[0].trim())
                    .collect(Collectors.toList());

            // Iterate through each set
            for (String setName : setNames) {
                Statement statement = new Statement();
                statement.setNamespace(namespace);
                statement.setSetName(setName);

                // Fetch all records (bins) in the set
                RecordSet recordSet = client.query(clientPolicy, statement);
                while (recordSet.next()) {
                    Map<String, Object> data = new HashMap<>();
                    Key key = recordSet.getKey();
                    data.put("set_name", setName);
                    data.put("key", key.userKey.getObject());
                    
                    // Fetch all bins in the record
                    Bin[] bins = recordSet.getRecord().bins;
                    for (Bin bin : bins) {
                        data.put(bin.name, bin.value.getObject());
                    }

                    result.add(data);
                }
                recordSet.close();
            }

            client.close();

            return result;
        } catch (AerospikeException e) {
            e.printStackTrace(); // Handle the exception appropriately in your application
            return null;
        }
    }
}
