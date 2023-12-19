import com.aerospike.client.AerospikeClient;
import com.aerospike.client.AerospikeException;
import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.aerospike.client.Record;
import com.aerospike.client.policy.ClientPolicy;
import com.aerospike.client.policy.QueryPolicy;
import com.aerospike.client.query.RecordSet;
import com.aerospike.client.query.Statement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AerospikeController {

    @GetMapping("/getAllSetDataAndWriteToFile")
    public void getAllSetDataAndWriteToFile(@RequestParam String namespace) {
        try {
            ClientPolicy clientPolicy = new ClientPolicy();
            AerospikeClient client = new AerospikeClient(clientPolicy, "10.216.34.32", 3000);

            // Fetch the set names for the given namespace using Info.request
            String response = Info.request(client.getNodes()[0], "sets");
            List<String> setNames = Arrays.stream(response.split(";"))
                    .map(setInfo -> setInfo.split(":")[0].trim())
                    .collect(Collectors.toList());

            // Iterate through each set
            for (String setName : setNames) {
                Statement statement = new Statement();
                statement.setNamespace(namespace);
                statement.setSetName(setName);

                // Use QueryPolicy instead of ClientPolicy
                QueryPolicy queryPolicy = new QueryPolicy();
                
                // Fetch all records (bins) in the set
                RecordSet recordSet = client.query(queryPolicy, statement);

                List<Map<String, Object>> result = new ArrayList<>();
                while (recordSet.next()) {
                    Map<String, Object> data = new HashMap<>();
                    Key key = recordSet.getKey();
                    if (key != null && key.userKey != null) {
                        data.put("key", key.userKey.getObject());
                    }
                    data.put("set_name", setName);
                    Record record = recordSet.getRecord();
                    if (record != null && record.bins != null) {
                        Map<String, Object> bins = record.bins;
                        data.putAll(bins);
                    }
                    result.add(data);
                }
                recordSet.close();

                // Write JSON data to file
                writeJsonToFile(setName, result);
            }

            client.close();
        } catch (AerospikeException e) {
            e.printStackTrace(); // Handle the exception appropriately in your application
        }
    }

    private void writeJsonToFile(String setFileName, List<Map<String, Object>> jsonData) {
        try {
            // Convert the list of maps to a JSON string
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(jsonData);

            // Specify the file path using the set name
            String filePath = "/your/folder/path/" + setFileName + ".json";

            // Write the JSON string to the file
            try (FileWriter fileWriter = new FileWriter(filePath);
                 BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
                bufferedWriter.write(jsonString);
            }

            System.out.println("JSON data written to file: " + filePath);
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception appropriately in your application
        }
    }
}
