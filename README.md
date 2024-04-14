import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "payment.gateway.cybersource")
public class CyberSourceConfig {
    private String url;
    private String host;
    private String signatureKeyId;
    private String signatureAlgorithm;
    private String signatureHeaders;
    private String signatureSignature;
    @Value("${payment.gateway.cybersource.digest}")
    private String digest;
    private String merchantId;
    private String date;

    // Getters and setters
}


payment.gateway.cybersource.url=https://apitest.cybersource.com/risk/v1/authentications
payment.gateway.cybersource.host=apitest.cybersource.com
payment.gateway.cybersource.signatureKeyId=6ad422b0-2f12-402b-b8e7-f883c21c864c
payment.gateway.cybersource.signatureAlgorithm=HmacSHA256
payment.gateway.cybersource.signatureHeaders=host request-target digest v-c-merchant-id
payment.gateway.cybersource.signatureSignature=GNT/QDj+zyWFW7kdsmtSYHJFag72NHz5HSg5G9nipVY=
payment.gateway.cybersource.digest=SHA-256=e+TU0fMjfwpaimwurUfCQUiWv+UWytA3cRaEuJwB4gI=
payment.gateway.cybersource.merchantId=hdfc_88888888
payment.gateway.cybersource.date=Thu, 21 Mar 2024 13:30:12 GMT


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PaymentService {

    @Autowired
    private CyberSourceConfig cyberSourceConfig;

    public void callPaymentGatewayApi() {
        String url = cyberSourceConfig.getUrl();
        String host = cyberSourceConfig.getHost();
        String signatureKeyId = cyberSourceConfig.getSignatureKeyId();
        String signatureAlgorithm = cyberSourceConfig.getSignatureAlgorithm();
        String signatureHeaders = cyberSourceConfig.getSignatureHeaders();
        String signatureSignature = cyberSourceConfig.getSignatureSignature();
        String digest = cyberSourceConfig.getDigest();
        String merchantId = cyberSourceConfig.getMerchantId();
        String date = cyberSourceConfig.getDate();

        // Create HttpHeaders
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("host", host);
        httpHeaders.add("signature", "keyid=\"" + signatureKeyId + "\", algorithm=\"" + signatureAlgorithm + "\", headers=\"" + signatureHeaders + "\", signature=\"" + signatureSignature + "\"");
        httpHeaders.add("digest", digest);
        httpHeaders.add("v-c-merchant-id", merchantId);
        httpHeaders.add("v-c-date", date);

        // Create HttpEntity with HttpHeaders
        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);

        // Make API call
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);

        // Process response as needed
        String responseBody = response.getBody();
        System.out.println("Response: " + responseBody);
    }
}



