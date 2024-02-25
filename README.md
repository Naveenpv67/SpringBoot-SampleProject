import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class WebClientUtils {

    private final WebClient webClient;

    public WebClientUtils(WebClient webClient) {
        this.webClient = webClient;
    }

    public String sendPostRequest(String url, String requestBody, MediaType contentType) {
        return webClient.post()
                .uri(url)
                .contentType(contentType)
                .body(BodyInserters.fromValue(requestBody))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}


public class YourServiceClass {

    private final WebClientUtils webClientUtils;

    public YourServiceClass(WebClientUtils webClientUtils) {
        this.webClientUtils = webClientUtils;
    }

    public String performHttpRequest(String url, String requestBody, MediaType contentType) {
        return webClientUtils.sendPostRequest(url, requestBody, contentType);
    }

    // Other methods and functionalities...
}


# environment.properties

base.url=https://10.226.163.7:9444/
url.path=com.ofss.fc.cz.hdfc.obp.webservice/CASABasicBalanceInquiryRestService/CASABasicBalanceInquiryRestWrapper
request.method=POST
content.type=json


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

public class YourServiceClass {

    private final WebClient obpWebClient;

    @Value("${base.url}")
    private String baseUrl;

    @Value("${url.path}")
    private String urlPath;

    @Value("${request.method}")
    private String requestMethod;  // Should be a valid HTTP method like "POST" or "GET"

    @Value("${content.type}")
    private String contentType;  // Should be "json" or "xml"

    public YourServiceClass(WebClient obpWebClient) {
        this.obpWebClient = obpWebClient;
    }

    private String constructFullUrl() {
        return baseUrl + urlPath;
    }

    private String webClientRequest(ViewBalanceRequest requestBody) {
        WebClient.RequestBodySpec requestSpec;

        if ("POST".equalsIgnoreCase(requestMethod)) {
            requestSpec = obpWebClient.post();
        } else if ("GET".equalsIgnoreCase(requestMethod)) {
            requestSpec = obpWebClient.get();
        } else {
            throw new IllegalArgumentException("Invalid request method: " + requestMethod);
        }

        return requestSpec
                .uri(constructFullUrl())
                .contentType(getContentType())
                .body(BodyInserters.fromValue(requestBody))
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> log.info("Successful response from OBP: {}", response))
                .doOnError(ResponseStatusException.class, error -> {
                    log.error("OBP response error: ", error);
                    throw error;
                })
                .block(Duration.ofSeconds(60));
    }

    private MediaType getContentType() {
        if ("json".equalsIgnoreCase(contentType)) {
            return MediaType.APPLICATION_JSON;
        } else if ("xml".equalsIgnoreCase(contentType)) {
            return MediaType.APPLICATION_XML;
        } else {
            throw new IllegalArgumentException("Invalid content type: " + contentType);
        }
    }

    // Other methods and functionalities...
}




