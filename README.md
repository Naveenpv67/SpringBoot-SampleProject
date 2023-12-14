import com.aerospike.client.*;
import com.aerospike.client.policy.QueryPolicy;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashMap;
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

    public void exportSetsIndexesAndData(String namespace) {
        AerospikeClient client = null;

        try {
            client = new AerospikeClient(new ClientPolicy(), hosts.split(":")[0], Integer.parseInt(hosts.split(":")[1]));

            String setsInfo = Info.request(client.getNodes()[0], "sets", namespace);
            String indexesInfo = Info.request(client.getNodes()[0], "sindex", namespace);

            Map<String, Object> dataInfo = new HashMap<>();
            QueryPolicy queryPolicy = new QueryPolicy();
            Statement stmt = new Statement();
            stmt.setNamespace(namespace);

            RecordSet recordSet = client.query(queryPolicy, stmt);

            try {
                while (recordSet.next()) {
                    Key key = recordSet.getKey();
                    Record record = recordSet.getRecord();

                    dataInfo.put(key.userKey.toString(), record.bins);
                }
            } finally {
                recordSet.close();
            }

            Map<String, Object> result = new HashMap<>();
            result.put("setsInfo", setsInfo);
            result.put("indexesInfo", indexesInfo);
            result.put("dataInfo", dataInfo);

            // Write to JSON file
            String jsonFileName = exportDirectory + File.separator + "exported_sets_indexes_data.json";
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(new File(jsonFileName), result);
        } catch (Exception e) {
            throw new RuntimeException("Error exporting sets, indexes, and data from Aerospike.", e);
        } finally {
            if (client != null && !client.isConnected()) {
                client.close();
            }
        }
    }
}


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

    @GetMapping("/setsIndexesAndData/{namespace}")
    public void exportSetsIndexesAndData(@PathVariable String namespace) {
        aerospikeInfoService.exportSetsIndexesAndData(namespace);
    }
}

