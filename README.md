import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Info;
import com.aerospike.client.policy.ClientPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/index/export")
public class IndexExportController {

    @Value("${spring.data.aerospike.hosts}")
    private String aerospikeHosts;

    @GetMapping("/{namespace}")
    public String exportAllIndexes(@PathVariable String namespace) {
        AerospikeClient client = null;

        try {
            client = new AerospikeClient(new ClientPolicy(), aerospikeHosts.split(":")[0], Integer.parseInt(aerospikeHosts.split(":")[1]));

            String indexesInfo = Info.request(client.getNodes()[0], "sindex", namespace);

            return indexesInfo;
        } catch (Exception e) {
            throw new RuntimeException("Error exporting indexes from Aerospike.", e);
        } finally {
            if (client != null && !client.isConnected()) {
                client.close();
            }
        }
    }
}
