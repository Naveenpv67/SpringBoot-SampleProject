import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
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

        logRequestDetails(request);

        return chain.filter(exchange).then(Mono.fromRunnable(() -> logResponseDetails(exchange)));
    }

    private void logRequestDetails(ServerHttpRequest request) {
        logger.info("Request path: {}", request.getPath());
        logger.info("Request method: {}", request.getMethod());
        logger.info("Request headers: {}", request.getHeaders());
        request.getBody().subscribe(body -> logger.info("Request body: {}", body.toString()));
    }

    private void logResponseDetails(ServerWebExchange exchange) {
        ServerHttpResponse originalResponse = exchange.getResponse();

        // Decorate the response to log the body
        ServerHttpResponseDecorator responseDecorator = new ServerHttpResponseDecorator(originalResponse) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                Flux<? extends DataBuffer> flux = Flux.from(body);

                // Log the response body
                return super.writeWith(flux.doOnNext(dataBuffer -> {
                    byte[] content = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(content);
                    logger.info("Response body: {}", new String(content, StandardCharsets.UTF_8));
                }));
            }
        };

        // Continue the filter chain with the decorated response
        exchange.mutate().response(responseDecorator).build();
    }
}
