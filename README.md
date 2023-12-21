import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.aerospike.client.Record;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AerospikeService {

    @Autowired
    private AerospikeClient aerospikeClient;

    public List<String> getRecords(String namespace, String set) {
        List<String> jsonRecords = new ArrayList<>();

        RecordSet recordSet = aerospikeClient.query(null,
                new Statement().setNamespace(namespace).setSetName(set).setBinNames("field1"));

        while (recordSet.next()) {
            Key key = recordSet.getKey();
            Record record = aerospikeClient.get(null, key);
            // Create a map to represent the record
            Map<String, Object> recordMap = new HashMap<>();
            recordMap.put("primaryKey", key.userKey.getObject());
            recordMap.put("field1", record.getString("field1"));
            // Add other fields as needed

            // Convert map to JSON string
            String jsonString = convertMapToJson(recordMap);
            
            // Add the JSON string to the list
            jsonRecords.add(jsonString);
        }

        return jsonRecords;
    }

    private String convertMapToJson(Map<String, Object> map) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            // Handle exception (e.g., log it or throw a custom exception)
            e.printStackTrace();
            return "{}"; // Return an empty JSON object if conversion fails
        }
    }
}
