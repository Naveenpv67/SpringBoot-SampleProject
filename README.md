import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

public class YourServiceClass {

    private final WebClient obpWebClient;

    // Other fields and methods...

    private String webClientSoapRequest(String xmlRequestBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);

        return obpWebClient
                .method(HttpMethod.POST)
                .uri(constructFullUrl())
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .body(BodyInserters.fromValue(xmlRequestBody))
                .exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().isError()) {
                        return clientResponse.bodyToMono(String.class)
                                .flatMap(errorResponseBody ->
                                        Mono.error(new ResponseStatusException(clientResponse.statusCode(), errorResponseBody)));
                    } else {
                        return clientResponse.bodyToMono(String.class);
                    }
                })
                .doOnSuccess(response -> log.info("Successful response from OBP: {}", response))
                .doOnError(ResponseStatusException.class, error -> {
                    log.error("OBP response error: ", error);
                    throw error;
                })
                .block(Duration.ofSeconds(60));
    }

    // Other methods and functionalities...
}
