import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class CustomFilter implements GatewayFilter {

    private static final Logger logger = LoggerFactory.getLogger(CustomFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        logRequestDetails(request);

        return chain.filter(exchange)
                .then(Mono.fromRunnable(() -> logResponseDetails(exchange)));
    }

    private void logRequestDetails(ServerHttpRequest request) {
        logger.info("Request path: {}", request.getPath());
        logger.info("Request method: {}", request.getMethod());
        logger.info("Request headers: {}", request.getHeaders());
        request.getBody().subscribe(body -> logger.info("Request body: {}", body.toString()));
    }

    private void logResponseDetails(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();

        // Extract and log response body (if present)
        exchange.getResponse().getBody()
                .doOnNext(body -> logger.info("Response body: {}", body.toString()))
                .subscribe();
    }
}
