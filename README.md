// Inside the same controller class or a separate controller class
@RestController
@RequestMapping("/api/confirm-import")
public class ConfirmImportController {

    @Value("${aerospike.host}")
    private String aerospikeHost;

    @Value("${aerospike.port}")
    private int aerospikePort;

    @GetMapping("/{setName}")
    public ResponseEntity<List<Map<String, Object>>> confirmImport(@PathVariable String setName) {
        try {
            // Connect to Aerospike
            AerospikeClient aerospikeClient = new AerospikeClient(aerospikeHost, aerospikePort);

            // Query Aerospike for all records in the specified set
            Statement statement = new Statement();
            statement.setNamespace("test"); // Set your Aerospike namespace
            statement.setSetName(setName);

            RecordSet recordSet = aerospikeClient.query(null, statement);

            // Process the records and convert them to a list of maps
            List<Map<String, Object>> records = new ArrayList<>();
            while (recordSet.next()) {
                Record record = recordSet.getRecord();
                Map<String, Object> recordMap = record != null ? record.bins : Collections.emptyMap();
                records.add(recordMap);
            }

            // Close Aerospike connection
            aerospikeClient.close();

            return new ResponseEntity<>(records, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
