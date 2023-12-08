<!-- Add Aerospike Java client dependency -->
<dependency>
    <groupId>com.aerospike</groupId>
    <artifactId>aerospike-client</artifactId>
    <version>5.2.0</version>
</dependency>

<!-- Spring Boot dependencies -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>



# Aerospike configuration
aerospike.host=127.0.0.1
aerospike.port=3000



import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@SpringBootApplication
public class JsonImportApplication {

    public static void main(String[] args) {
        SpringApplication.run(JsonImportApplication.class, args);
    }

    @RestController
    @RequestMapping("/api/json-import")
    public static class JsonImportController {

        @Value("${aerospike.host}")
        private String aerospikeHost;

        @Value("${aerospike.port}")
        private int aerospikePort;

        @PostMapping
        public ResponseEntity<String> importJsonData(
                @RequestParam("file") MultipartFile file
        ) {
            try {
                // Connect to Aerospike
                AerospikeClient aerospikeClient = new AerospikeClient(aerospikeHost, aerospikePort);

                // Extract set name from the filename
                String originalFilename = file.getOriginalFilename();
                String setName = originalFilename != null ? originalFilename.split("\\.")[0] : "defaultSet";

                // Read JSON data from the file
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(file.getBytes());

                // Import JSON data into Aerospike
                importDataIntoAerospike(jsonNode, aerospikeClient, setName);

                // Close Aerospike connection
                aerospikeClient.close();

                return new ResponseEntity<>("Data imported successfully into set: " + setName, HttpStatus.OK);
            } catch (IOException e) {
                e.printStackTrace();
                return new ResponseEntity<>("Error reading the JSON file: " + e.getMessage(), HttpStatus.BAD_REQUEST);
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>("Error importing data into Aerospike: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        private void importDataIntoAerospike(JsonNode jsonNode, AerospikeClient aerospikeClient, String setName) {
            // Assuming the JSON represents a list of objects
            for (JsonNode objectNode : jsonNode) {
                // Assuming each object has a unique identifier field named "id"
                String id = objectNode.get("id").asText();

                // Create Aerospike key
                Key key = new Key("test", setName, id);

                // Create Aerospike bins from JSON fields
                for (String fieldName : objectNode.fieldNames()) {
                    Bin bin = new Bin(fieldName, objectNode.get(fieldName).asText());
                    aerospikeClient.put(null, key, bin);
                }
            }
        }
    }
}

