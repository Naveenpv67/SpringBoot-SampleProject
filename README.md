import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Controller
@RequestMapping("/api")
public class YourController {

    private static final Logger log = LoggerFactory.getLogger(YourController.class);

    @Autowired
    private WebClient obpWebClient;

    @PostMapping("/yourEndpoint")
    @ResponseBody
    public ResponseEntity<String> postData(@RequestBody YourRequestBodyClass requestBody) {
        String mainResponse;

        try {
            mainResponse = webClientPost(requestBody);
            return ResponseEntity.ok(mainResponse);
        } catch (ResponseStatusException e) {
            log.error("Error making OBP API call: ", e);
            return ResponseEntity.status(e.getStatus()).body(e.getMessage());
        }
    }

    private String webClientPost(YourRequestBodyClass requestBody) {
        return obpWebClient.post()
                .uri("/yourPostEndpoint") // Adjust the endpoint accordingly
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(requestBody))
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(errorResponseBody -> Mono.error(new ResponseStatusException(clientResponse.statusCode(), errorResponseBody)))
                )
                .bodyToMono(String.class)
                .doOnSuccess(response -> log.info("Successful response from OBP: {}", response))
                .doOnError(ResponseStatusException.class, error -> {
                    log.error("OBP response error: ", error);
                    throw error;
                })
                .block(Duration.ofSeconds(60));
    }
}
