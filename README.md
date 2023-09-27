import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        // Create a custom HttpClient with SSL verification disabled
        CloseableHttpClient httpClient = HttpClients.custom()
                .setSslcontext(SSLContexts.custom()
                        .loadTrustMaterial(new TrustSelfSignedStrategy())
                        .build())
                .build();

        // Create a ClientHttpRequestFactory using the custom HttpClient
        ClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);

        // Set the ClientHttpRequestFactory for the RestTemplate
        return builder.requestFactory(() -> factory).build();
    }
}






<dependency>
    <groupId>org.apache.httpcomponents</groupId>
    <artifactId>httpclient</artifactId>
    <version>4.5.13</version> <!-- Use the latest version -->
</dependency>
<dependency>
    <groupId>org.apache.httpcomponents</groupId>
    <artifactId>httpcore</artifactId>
    <version>4.4.14</version> <!-- Use the latest version -->
</dependency>
<dependency>
    <groupId>org.apache.httpcomponents</groupId>
    <artifactId>httpasyncclient</artifactId>
    <version>4.1.4</version> <!-- Use the latest version -->
</dependency>
