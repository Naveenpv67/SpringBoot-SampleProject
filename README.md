// ... (existing imports)

@Service
public class AerospikeRWService {

    // ... (existing fields)

    public List<String> getAllDatasets(String namespace) {
        try {
            // Query Aerospike for all set names in the specified namespace
            Node[] nodes = aerospikeClient.getNodes();
            Set<String> datasetNames = new HashSet<>();
            for (Node node : nodes) {
                Map<Namespace, NamespaceStatistics> namespaceStats = node.getNamespaceMap();
                NamespaceStatistics stats = namespaceStats.get(new Namespace(namespace));
                if (stats != null) {
                    datasetNames.addAll(stats.getSets());
                }
            }

            return new ArrayList<>(datasetNames);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // ... (remaining methods)
}
