import java.security.cert.X509Certificate;
import java.security.cert.CertificateException;
import javax.net.ssl.X509TrustManager;

public class InsecureX509TrustManager implements X509TrustManager {
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        // Accept all client certificates
    }

    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        // Accept all server certificates
    }

    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}


import feign.Client;
import feign.httpclient.ApacheHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomFeignConfiguration {
    @Bean
    public Client feignClient() {
        return new ApacheHttpClient(customHttpClient());
    }

    private CloseableHttpClient customHttpClient() {
        try {
            return HttpClients.custom()
                    .setSslcontext(SSLContextBuilder.create().loadTrustMaterial(new InsecureX509TrustManager()).build())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Error configuring Feign client with custom TrustManager.", e);
        }
    }
}


