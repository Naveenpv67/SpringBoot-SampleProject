import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class EncryptionService {

    private final WebClient webClient;

    @Autowired
    public EncryptionService(WebClient webClient) {
        this.webClient = webClient;
    }

    public String encryptMessage(String devicePrKey, String serverPbKey, String body, String token, String tokenType, String salt) {
        return webClient.post()
                .uri("https://dev2.mbv3.hdfcbank.com/crypter/encrypt")
                .header("Device-Id", "test125-naveen")
                .bodyValue("device_pr_key=" + devicePrKey +
                        "&server_pb_key=" + serverPbKey +
                        "&body=" + body +
                        "&token=" + token +
                        "&token_type=" + tokenType +
                        "&salt=" + salt)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
