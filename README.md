import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.security.NoSuchAlgorithmException;
import java.security.KeyManagementException;
import java.security.cert.CertificateException;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) throws NoSuchAlgorithmException, KeyManagementException {
        // Create a custom TrustManager that trusts all certificates
        TrustManager[] trustAllCertificates = new TrustManager[]{
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
        };

        // Create an SSL context that trusts all certificates
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCertificates, new java.security.SecureRandom());

        // Create a RestTemplate with a custom SSL context that disables SSL verification
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setHttpClient(HttpClients.custom()
                .setSSLContext(sslContext)


                
                .build());

        return builder.requestFactory(() -> factory).build();
    }
}




