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

            String[] hostPort = aerospikeHosts.split(":");
            String aerospikeHost = hostPort[0];
            int aerospikePort = Integer.parseInt(hostPort[1]);

            return new AerospikeClient(clientPolicy, aerospikeHost, aerospikePort);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize AerospikeClient", e);
        }
    }
}
