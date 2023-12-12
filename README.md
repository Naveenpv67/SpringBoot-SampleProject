import com.aerospike.client.Record;
import com.aerospike.client.query.Statement;
import com.aerospike.client.query.RecordSet;

// ... (existing imports)

@Service
public class AerospikeRWService {

    // ... (existing fields)

    public Map<String, List<Map<String, Object>>> getAllDatasets(String namespace) {
        try {
            // Query Aerospike for all set names and their data in the specified namespace
            Node[] nodes = aerospikeClient.getNodes();
            Map<String, List<Map<String, Object>>> datasetMap = new HashMap<>();
            for (Node node : nodes) {
                Set<String> setNames = node.getSets(namespace);
                for (String setName : setNames) {
                    List<Map<String, Object>> setData = getSetData(namespace, setName);
                    datasetMap.put(setName, setData);
                }
            }

            return datasetMap;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyMap();
        }
    }

    private List<Map<String, Object>> getSetData(String namespace, String setName) {
        try {
            List<Map<String, Object>> setData = new ArrayList<>();
            
            // Create a statement to scan the set
            Statement statement = new Statement();
            statement.setNamespace(namespace);
            statement.setSetName(setName);

            // Execute the scan operation
            RecordSet recordSet = aerospikeClient.query(null, statement);
            while (recordSet.next()) {
                Record record = recordSet.getRecord();
                if (record != null) {
                    setData.add(record.bins);
                }
            }

            return setData;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // ... (remaining methods)
}
