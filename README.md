import com.aerospike.client.AerospikeClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AerospikeConfig {

    @Value("${aerospike.host}")
    private String aerospikeHost;

    @Value("${aerospike.port}")
    private int aerospikePort;

    @Bean(destroyMethod = "close")
    public AerospikeClient aerospikeClient() {
        return new AerospikeClient(aerospikeHost, aerospikePort);
    }
}
