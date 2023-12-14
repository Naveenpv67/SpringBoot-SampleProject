import com.aerospike.client.AerospikeClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Map;

@Service
public class AerospikeInfoService {

    @Value("${spring.data.aerospike.hosts}")
    private String hosts;

    @Value("${spring.data.aerospike.username}")
    private String username;

    @Value("${spring.data.aerospike.password}")
    private String password;

    @Value("${export.directory}")
    private String exportDirectory;

    public void exportSetsAndIndexes(String namespace) {
        ClientPolicy clientPolicy = new ClientPolicy();
        clientPolicy.user = username;
        clientPolicy.password = password;

        try (AerospikeClient client = new AerospikeClient(clientPolicy, hosts.split(":")[0], Integer.parseInt(hosts.split(":")[1]))) {
            String setsInfo = Info.request(client.queryPolicyDefault, hosts, "sets");
            String indexesInfo = Info.request(client.queryPolicyDefault, hosts, "sindex");

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> result = Map.of("setsInfo", setsInfo, "indexesInfo", indexesInfo);

            // Write to JSON file
            String jsonFileName = exportDirectory + File.separator + "exported_data.json";
            objectMapper.writeValue(new File(jsonFileName), result);
        } catch (Exception e) {
            throw new RuntimeException("Error exporting sets and indexes information from Aerospike.", e);
        }
    }
}



export.directory=/path/to/export


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/export")
public class ExportController {

    @Autowired
    private AerospikeInfoService aerospikeInfoService;

    @GetMapping("/{namespace}")
    public void exportSetsAndIndexes(@PathVariable String namespace) {
        aerospikeInfoService.exportSetsAndIndexes(namespace);
    }
}

