import com.aerospike.client.AerospikeClient;
import com.aerospike.client.policy.ClientPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.aerospike.config.AerospikeDataSettings;
import org.springframework.data.aerospike.core.AerospikeTemplate;

@Configuration
public class AerospikeConfig {

    @Value("${spring.data.aerospike.hosts}")
    private String hosts;

    @Value("${spring.data.aerospike.username}")
    private String username;

    @Value("${spring.data.aerospike.password}")
    private String password;

    @Value("${spring.data.aerospike.namespace}")
    private String namespace;

    @Bean
    public AerospikeTemplate aerospikeTemplate(AerospikeClient aerospikeClient,
                                               AerospikeDataSettings aerospikeDataSettings) {
        return new AerospikeTemplate(aerospikeClient, aerospikeDataSettings.getNamespace());
    }

    @Bean
    public AerospikeClient aerospikeClient(AerospikeDataSettings aerospikeDataSettings) {
        ClientPolicy clientPolicy = new ClientPolicy();
        clientPolicy.user = username;
        clientPolicy.password = password;

        return new AerospikeClient(clientPolicy, hosts);
    }

    @Bean
    public AerospikeDataSettings aerospikeDataSettings() {
        AerospikeDataSettings settings = new AerospikeDataSettings();
        settings.setNamespace(namespace);
        return settings;
    }

    @Bean
    public ClientPolicy aerospikeClientPolicy(AerospikeClient aerospikeClient) {
        return aerospikeClient.getClientPolicy();
    }
}
