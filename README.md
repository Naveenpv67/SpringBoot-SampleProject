// Inside the same controller class or a separate controller class
@RestController
@RequestMapping("/api/aerospike")
public class AerospikeController {

    @Value("${aerospike.host}")
    private String aerospikeHost;

    @Value("${aerospike.port}")
    private int aerospikePort;

    @Autowired
    private AerospikeClient aerospikeClient;

    @GetMapping("/datasets/{namespace}")
    public ResponseEntity<List<String>> getAllDatasets(@PathVariable String namespace) {
        try {
            // Query Aerospike for all set names in the specified namespace
            Node[] nodes = aerospikeClient.getNodes();
            Set<String> datasetNames = new HashSet<>();
            for (Node node : nodes) {
                Partition[] partitions = node.getPartitions();
                for (Partition partition : partitions) {
                    Set<String> setNames = partition.getSets();
                    datasetNames.addAll(setNames);
                }
            }

            return new ResponseEntity<>(new ArrayList<>(datasetNames), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/set/{namespace}/{setName}")
    public ResponseEntity<String> deleteSet(@PathVariable String namespace, @PathVariable String setName) {
        try {
            // Delete the specified set
            aerospikeClient.truncate(null, namespace, setName, null);

            return new ResponseEntity<>("Set '" + setName + "' deleted successfully.", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error deleting set '" + setName + "': " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/bulk-delete/{namespace}")
    public ResponseEntity<String> bulkDeleteSets(@PathVariable String namespace) {
        try {
            // Delete all sets in the specified namespace
            Node[] nodes = aerospikeClient.getNodes();
            for (Node node : nodes) {
                Partition[] partitions = node.getPartitions();
                for (Partition partition : partitions) {
                    Set<String> setNames = partition.getSets();
                    for (String setName : setNames) {
                        aerospikeClient.truncate(null, namespace, setName, null);
                    }
                }
            }

            return new ResponseEntity<>("All sets in namespace '" + namespace + "' deleted successfully.", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error deleting sets in namespace '" + namespace + "': " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
