spring.zipkin.base-url=http://localhost:9411
spring.sleuth.sampler.probability=1.0
spring.sleuth.tracing.enabled=true


@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

    private final RestTemplate restTemplate;

    @Autowired
    public CouService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
