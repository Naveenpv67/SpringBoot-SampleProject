import com.aerospike.client.AerospikeClient;
import com.aerospike.client.policy.ClientPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AerospikeConfig {

    @Value("${spring.data.aerospike.hosts}")
    private String aerospikeHosts;

    @Value("${spring.data.aerospike.username}")
    private String aerospikeUsername;

    @Value("${spring.data.aerospike.password}")
    private String aerospikePassword;

    @Bean(destroyMethod = "close")
    public AerospikeClient aerospikeClient() {
        try {
            ClientPolicy clientPolicy = new ClientPolicy();
            clientPolicy.user = aerospikeUsername;
            clientPolicy.password = aerospikePassword;

            return new AerospikeClient(clientPolicy, aerospikeHosts);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize AerospikeClient", e);
        }
    }
}
