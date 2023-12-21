import com.aerospike.client.AerospikeClient;
import com.aerospike.client.AerospikeException;
import com.aerospike.client.Key;
import com.aerospike.client.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AerospikeService {

    @Autowired
    private AerospikeClient aerospikeClient;

    public List<Map<String, Object>> getRecords(String namespace, String set) {
        List<Map<String, Object>> recordsList = new ArrayList<>();

        try {
            RecordSet recordSet = aerospikeClient.query(null,
                    new Statement().setNamespace(namespace).setSetName(set));

            while (recordSet.next()) {
                Key key = recordSet.getKey();
                Record record = aerospikeClient.get(null, key);

                // Convert each record to a map with a linked map to maintain order
                Map<String, Object> recordMap = new LinkedHashMap<>();

                // Check if "PK" exists in bins before adding it
                if (record != null && record.bins != null && record.bins.containsKey("PK")) {
                    recordMap.put("PK", key.userKey.getObject());
                }

                // Iterate over all bins and add them to the map
                if (record != null && record.bins != null) {
                    for (Map.Entry<String, Object> binEntry : record.bins.entrySet()) {
                        // Skip adding "PK" again if it was already added
                        if (!"PK".equals(binEntry.getKey())) {
                            recordMap.put(binEntry.getKey(), binEntry.getValue());
                        }
                    }
                }

                // Add the record map to the list
                recordsList.add(recordMap);
            }
        } catch (AerospikeException e) {
            // Handle Aerospike exceptions (e.g., log them, throw a custom exception)
            e.printStackTrace();
            // You might want to rethrow or handle the exception based on your requirements
        } catch (Exception e) {
            // Handle other exceptions
            e.printStackTrace();
            // You might want to rethrow or handle the exception based on your requirements
        }

        return recordsList;
    }
}
