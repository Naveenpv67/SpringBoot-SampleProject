import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.transport.WebServiceMessageSender;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;
import org.springframework.ws.transport.http.HttpsUrlConnectionMessageSender;
import org.springframework.ws.transport.http.HttpUrlConnectionMessageSender;
import org.springframework.ws.transport.http.WebServiceMessageSenderConnectionCallback;

@Configuration
public class AccountStatementSoapConnectorConfig {

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("com.hdfcbank.consumingwebservice.wsdl");
        return marshaller;
    }

    @Bean
    public SoapConnector soapConnector(Jaxb2Marshaller marshaller) {
        SoapConnector client = new SoapConnector();
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        client.setDefaultUri("https://10.226.163.7:9444/com.ofss.fc.cz.hdfc.obp.webservice/AccountStatementSpi");
        
        // Set a custom message sender to bypass SSL certificate validation
        client.setMessageSender(bypassSslCertificateValidation());
        
        return client;
    }

    @Bean
    public WebServiceMessageSender bypassSslCertificateValidation() {
        // Create a custom TrustManager that trusts all certificates
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new X509TrustManager[] { trustManager }, null);

            // Create a custom WebServiceMessageSender with the bypassed SSL context
            return new HttpComponentsMessageSender(HttpClients.custom().setSslcontext(sslContext).build());

        } catch (Exception e) {
            throw new RuntimeException("Failed to configure custom SSL context.", e);
        }
    }
}
