import com.aerospike.client.AerospikeClient;
import com.aerospike.client.policy.ClientPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AerospikeConfig {

    @Value("${aerospike.host}")
    private String aerospikeHost;

    @Value("${aerospike.port}")
    private int aerospikePort;

    @Value("${aerospike.username}")
    private String aerospikeUsername;

    @Value("${aerospike.password}")
    private String aerospikePassword;

    @Bean(destroyMethod = "close")
    public AerospikeClient aerospikeClient() {
        ClientPolicy clientPolicy = new ClientPolicy();
        clientPolicy.user = aerospikeUsername;
        clientPolicy.password = aerospikePassword;
        return new AerospikeClient(clientPolicy, aerospikeHost, aerospikePort);
    }
}
