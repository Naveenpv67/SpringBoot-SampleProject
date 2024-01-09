import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggingGatewayFilterFactory extends AbstractGatewayFilterFactory<LoggingGatewayFilterFactory.Config> {

    public LoggingGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // Pre-filter logic for request logging
            System.out.println("Request URI: " + exchange.getRequest().getURI());
            System.out.println("Request Method: " + exchange.getRequest().getMethod());

            // Extract and print request body
            exchange.getRequest().getBody().subscribe(data -> {
                byte[] bytes = new byte[data.readableByteCount()];
                data.read(bytes);
                System.out.println("Request Body: " + new String(bytes));
            });

            // Continue the filter chain
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                // Post-filter logic for response logging
                System.out.println("Response Status Code: " + exchange.getResponse().getStatusCode());

                // Extract and print response body
                exchange.getResponse().getBody().subscribe(data -> {
                    byte[] bytes = new byte[data.readableByteCount()];
                    data.read(bytes);
                    System.out.println("Response Body: " + new String(bytes));
                });
            }));
        };
    }

    public static class Config {
        // Configuration properties can be added here if needed
    }
}




@Bean
public RouteLocator customRouteLocator(RouteLocatorBuilder builder, LoggingGatewayFilterFactory loggingFilterFactory) {
    return builder.routes()
            .route("service1", r -> r
                    .path("/service1/**")
                    .filters(f -> f.filter(loggingFilterFactory.apply(new LoggingGatewayFilterFactory.Config())))
                    .uri("http://localhost:8081")
            )
            .route("service2", r -> r
                    .path("/service2/**")
                    .filters(f -> f.filter(loggingFilterFactory.apply(new LoggingGatewayFilterFactory.Config())))
                    .uri("http://localhost:8082")
            )
            .build();
}


