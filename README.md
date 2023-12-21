import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Key;
import com.aerospike.client.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AerospikeService {

    @Autowired
    private AerospikeClient aerospikeClient;

    public List<Map<String, Object>> getRecords(String namespace, String set) {
        List<Map<String, Object>> recordsList = new ArrayList<>();

        RecordSet recordSet = aerospikeClient.query(null,
                new Statement().setNamespace(namespace).setSetName(set));

        while (recordSet.next()) {
            Key key = recordSet.getKey();
            Record record = aerospikeClient.get(null, key);

            // Convert each record to a map
            Map<String, Object> recordMap = new HashMap<>();
            recordMap.put("primaryKey", key.userKey.getObject());

            // Iterate over all bins and add them to the map
            for (Map.Entry<String, Object> binEntry : record.bins.entrySet()) {
                recordMap.put(binEntry.getKey(), binEntry.getValue());
            }

            // Add the record map to the list
            recordsList.add(recordMap);
        }

        return recordsList;
    }
}
