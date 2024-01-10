import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    @Configuration
    public class GatewayConfig {

        @Bean
        public RouteLocatorBuilder routeLocatorBuilder() {
            return new RouteLocatorBuilder();
        }

        @Bean
        public RouteLocator customRouteLocator(RouteLocatorBuilder builder, CustomFilter customFilter) {
            return builder.routes()
                    .route("example-route", r -> r.path("/example")
                            .filters(f -> f.filter(customFilter))
                            .uri("http://example.com"))
                    .build();
        }
    }

    @Bean
    public CustomFilter customFilter() {
        return new CustomFilter();
    }
}




import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class CustomFilter implements GatewayFilter {

    private static final Logger logger = LoggerFactory.getLogger(CustomFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        // Log request details
        logger.info("Request path: {}", request.getPath());
        logger.info("Request method: {}", request.getMethod());
        logger.info("Request headers: {}", request.getHeaders());

        // Log request body (if present)
        request.getBody().subscribe(body -> {
            logger.info("Request body: {}", body.toString()); // Adjust based on your needs
        });

        // Proceed with the filter chain
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            // Log response details
            logger.info("Response status code: {}", response.getStatusCode());
            logger.info("Response headers: {}", response.getHeaders());

            // Log response body (if present)
            response.getBody().subscribe(body -> {
                logger.info("Response body: {}", body.toString()); // Adjust based on your needs
            });
        }));
    }
}
