import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class CustomFilter implements GatewayFilter {

    private static final Logger logger = LoggerFactory.getLogger(CustomFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        logRequestDetails(request);

        return chain.filter(exchange)
                .then(Mono.fromRunnable(() -> logResponseDetails(response)));
    }

    private void logRequestDetails(ServerHttpRequest request) {
        logger.info("Request path: {}", request.getPath());
        logger.info("Request method: {}", request.getMethod());
        logger.info("Request headers: {}", request.getHeaders());
        request.getBody().subscribe(body -> logger.info("Request body: {}", body.toString()));
    }

    private void logResponseDetails(ServerHttpResponse response) {
        logger.info("Response status code: {}", response.getStatusCode());
        logger.info("Response headers: {}", response.getHeaders());

        // Log response body (if present)
        response.modifyResponseBody(String.class, String.class,
                (exchange, originalResponseBody) -> {
                    Flux<DataBuffer> cachedBody = Flux.from(originalResponseBody)
                            .doOnNext(dataBuffer -> {
                                byte[] content = new byte[dataBuffer.readableByteCount()];
                                dataBuffer.read(content);
                                logger.info("Response body: {}", new String(content, StandardCharsets.UTF_8));
                            });

                    return response.writeWith(cachedBody);
                }).subscribe();
    }
}
