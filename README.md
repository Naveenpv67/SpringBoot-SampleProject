import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        // Create a RestTemplate with custom ClientHttpRequestFactory
        return builder.requestFactory(this::clientHttpRequestFactory).build();
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() {
        // Create a SimpleClientHttpRequestFactory
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        
        // Disable SSL verification by setting setTrustAllTrusted(true)
        factory.setTrustAllTrusted(true);
        
        return factory;
    }
}
