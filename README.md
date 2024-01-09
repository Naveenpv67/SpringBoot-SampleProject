<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>spring-cloud-gateway</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <properties>
        <java.version>1.8</java.version>
        <spring-cloud.version>2020.0.2</spring-cloud.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-webflux</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-gateway</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>






import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    // Custom pre filter for request logging
    @Bean
    public GlobalFilter requestLoggingFilter() {
        return (exchange, chain) -> {
            // Pre-filter logic for request logging
            System.out.println("Request URI: " + exchange.getRequest().getURI());
            System.out.println("Request Method: " + exchange.getRequest().getMethod());

            // Extract and print request body
            ServerHttpRequest request = exchange.getRequest();
            request.getBody().subscribe(data -> {
                byte[] bytes = new byte[data.readableByteCount()];
                data.read(bytes);
                System.out.println("Request Body: " + new String(bytes));
            });

            return chain.filter(exchange);
        };
    }

    // Custom post filter for response logging
    @Bean
    public GlobalFilter responseLoggingFilter() {
        return (exchange, chain) -> {
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                // Post-filter logic for response logging
                System.out.println("Response Status Code: " + exchange.getResponse().getStatusCode());

                // Extract and print response body
                ServerHttpResponse response = exchange.getResponse();
                response.beforeCommit(() -> {
                    byte[] bytes = new byte[response.bufferFactory().buffer().readableByteCount()];
                    response.bufferFactory().buffer().read(bytes);
                    System.out.println("Response Body: " + new String(bytes));
                    return Mono.empty();
                });

            }));
        };
    }

    // Custom global filter for extracting and printing request and response bodies
    @Bean
    public GlobalFilter globalLoggingFilter() {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            // Extract and print request body
            request.getBody().subscribe(data -> {
                byte[] bytes = new byte[data.readableByteCount()];
                data.read(bytes);
                System.out.println("Global Request Body: " + new String(bytes));
            });

            // Continue with the filter chain
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                // Extract and print response body
                response.beforeCommit(() -> {
                    byte[] bytes = new byte[response.bufferFactory().buffer().readableByteCount()];
                    response.bufferFactory().buffer().read(bytes);
                    System.out.println("Global Response Body: " + new String(bytes));
                    return Mono.empty();
                });
            }));
        };
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("service1", r -> r
                        .path("/service1/**")
                        .filters(f -> f.filter(requestLoggingFilter()))
                        .filters(f -> f.filter(responseLoggingFilter()))
                        .uri("http://localhost:8081")
                )
                .route("service2", r -> r
                        .path("/service2/**")
                        .filters(f -> f.filter(requestLoggingFilter()))
                        .filters(f -> f.filter(responseLoggingFilter()))
                        .uri("http://localhost:8082")
                )
                .build();
    }
}


