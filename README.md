import com.aerospike.client.Info;

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
                Set<String> setNames = getSetNames(node, namespace);
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

    private Set<String> getSetNames(Node node, String namespace) {
        String response = Info.request(node, "sets", namespace);
        Set<String> setNames = new HashSet<>();
        if (response != null) {
            String[] lines = response.split(";");
            for (String line : lines) {
                String[] parts = line.split(":");
                if (parts.length > 1) {
                    setNames.add(parts[0].trim());
                }
            }
        }
        return setNames;
    }

    // ... (remaining methods)
}
