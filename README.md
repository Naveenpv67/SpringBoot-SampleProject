import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

public class YourServiceClass {

    private final WebClient obpWebClient;

    // Other fields and methods...

    private String webClientSoapRequest(String soapEnvelope) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_XML);

        return obpWebClient.post()
                .uri(constructFullUrl())
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .body(BodyInserters.fromValue(soapEnvelope))
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> log.info("Successful response from OBP: {}", response))
                .doOnError(ResponseStatusException.class, error -> {
                    log.error("OBP response error: ", error);
                    throw error;
                })
                .block(Duration.ofSeconds(60));
    }

    // Other methods and functionalities...
}
