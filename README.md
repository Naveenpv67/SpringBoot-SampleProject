import feign.Client;
import feign.httpclient.ApacheHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.ssl.SSLContextBuilder;

@Configuration
public class CustomFeignConfiguration {
    @Bean
    public Client feignClient() {
        return new ApacheHttpClient(disableSslValidation());
    }

    private CloseableHttpClient disableSslValidation() {
        try {
            return HttpClients.custom()
                    .setSslcontext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Error configuring Feign client with SSL validation disabled.", e);
        }
    }
}
