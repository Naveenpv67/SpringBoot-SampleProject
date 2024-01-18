import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

@Configuration
public class GatewayConfig {

    private String salt;
    private String serverPubKey;

    public String extractResponse(ServerWebExchange exchange, String outData) {
        ServerHttpRequest request = exchange.getRequest();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // Parse the JSON response
            JsonNode jsonNode = objectMapper.readTree(outData);

            // Extract values from the response
            JsonNode bodyNode = jsonNode.path("body");
            if (bodyNode.isObject()) {
                serverPubKey = bodyNode.path("serverPubKey").asText();
                salt = bodyNode.path("salt").asText();
            }

        } catch (Exception e) {
            // Handle exceptions as needed
            e.printStackTrace();
        }

        return outData;
    }

    // Getter methods for accessing the values from other parts of your application
    public String getSalt() {
        return salt;
    }

    public String getServerPubKey() {
        return serverPubKey;
    }
}
