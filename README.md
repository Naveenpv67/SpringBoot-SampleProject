import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class EncryptionService {

    private static final String ENCRYPT_ENDPOINT = "https://dev2.mbv3.hdfcbank.com/crypter/encrypt";
    private static final String DEVICE_ID_HEADER = "test125-naveen";

    public Mono<String> encryptMessage(String devicePrKey, String serverPbKey, String body, String token, String tokenType, String salt) {
        return WebClient.builder()
                .baseUrl(ENCRYPT_ENDPOINT)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .defaultHeader("Device-Id", DEVICE_ID_HEADER)
                .build()
                .post()
                .uri("")
                .body(BodyInserters.fromFormData("device_pr_key", devicePrKey)
                        .with("server_pb_key", serverPbKey)
                        .with("body", body)
                        .with("token", token)
                        .with("token_type", tokenType)
                        .with("salt", salt))
                .retrieve()
                .bodyToMono(String.class);
    }

    // Example usage
    public static void main(String[] args) {
        EncryptionService encryptionService = new EncryptionService();
        encryptionService.encryptMessage("PiPBMGEAINSISunTd4fW190A6cnQF¢NXqWOOJAQjZPY=""",
                                        "MFkwEwYHKoZIzj0CAQYIKOZIzj0DAQcDQgAERLVueLA27Iboy22piEnvNduD3AIqDAeLlinJ5kbOUMclvRALz5+F6VZHZQhTLb5KMamh Lebte07p0==",
                                        "{\"hashUserId\":\"d9c56347799146784812c67546ac9895.1a2dbc56ca5ec2448fcflff60fd038SA",
                                        "sessionDetails Resp.SessionToken",
                                        "LI",
                                        "cydkUd4nyjpBaJWofnZ6")
                .subscribe(System.out::println);
    }
}
