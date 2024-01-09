import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder, CustomLoggingFilter customLoggingFilter) {
        return builder.routes()
                .route("example_route", r -> r.path("/example")
                        .uri("http://example.com"))
                .build();
    }

    @Bean
    public CustomLoggingFilter customLoggingFilter() {
        return new CustomLoggingFilter();
    }
}

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class CustomLoggingFilter extends AbstractGatewayFilterFactory<CustomLoggingFilter.Config> {

    public CustomLoggingFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // Custom logic for pre-processing request
            System.out.println("Request Body: " + exchange.getRequest().getBody());

            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                // Custom logic for post-processing response
                System.out.println("Response Body: " + exchange.getResponse().getBody());
            }));
        };
    }

    public static class Config {
        // Configuration properties can be added here if needed
    }
}
