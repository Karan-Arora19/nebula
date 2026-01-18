package pse.nebula.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * WebClient configuration for reactive HTTP calls with LoadBalancer support
 */
@Configuration
public class WebClientConfig {

    /**
     * Create a WebClient.Builder bean for dependency injection
     */
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}

