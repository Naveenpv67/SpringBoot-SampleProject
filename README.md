import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@SpringBootApplication
public class MaxFieldsObjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(MaxFieldsObjectApplication.class, args);
    }
}

@RestController
class MaxFieldsObjectController {

    @PostMapping("/findMaxFieldsObject")
    public ResponseEntity<Map<String, Object>> findMaxFieldsObject(@RequestBody String jsonArray) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonArray);

            int maxFieldsCount = 0;
            JsonNode maxFieldsObject = null;

            Iterator<JsonNode> iterator = jsonNode.elements();

            while (iterator.hasNext()) {
                JsonNode obj = iterator.next();
                int fieldsCount = countFields(obj);

                if (fieldsCount > maxFieldsCount) {
                    maxFieldsCount = fieldsCount;
                    maxFieldsObject = obj;
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("maxFieldsObject", maxFieldsObject);
            result.put("fieldsCount", maxFieldsCount);

            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private int countFields(JsonNode node) {
        int count = node.size();
        Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            if (entry.getValue().isObject()) {
                count += countFields(entry.getValue());
            }
        }
        return count;
    }
}
