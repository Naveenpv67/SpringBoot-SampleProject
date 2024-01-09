import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
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

@Component
class CustomLoggingFilter implements GlobalFilter {
    @Override
    public Mono<Void> filter(ServerRequest request, ServerResponse response, GatewayFilterChain chain) {
        // Custom logic for pre-processing request
        System.out.println("Request Body: " + request.bodyToMono(String.class).block());

        return chain.filter(request).then(Mono.fromRunnable(() -> {
            // Custom logic for post-processing response
            System.out.println("Response Body: " + response.bodyToMono(String.class).block());
        }));
    }
}
