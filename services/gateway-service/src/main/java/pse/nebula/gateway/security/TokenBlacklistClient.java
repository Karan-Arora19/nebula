package pse.nebula.gateway.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Client for checking token blacklist status from user-service
 * Uses Spring Cloud LoadBalancer for service discovery
 */
@Component
public class TokenBlacklistClient {

    private static final Logger log = LoggerFactory.getLogger(TokenBlacklistClient.class);
    private static final String USER_SERVICE_LB_URL = "http://user-service";

    private final WebClient webClient;

    public TokenBlacklistClient(WebClient.Builder webClientBuilder,
                                ReactorLoadBalancerExchangeFilterFunction lbFunction) {
        this.webClient = webClientBuilder
                .filter(lbFunction)
                .build();
    }

    /**
     * Check if a token is blacklisted by calling user-service
     *
     * @param token JWT token to check
     * @return Mono<Boolean> true if blacklisted, false otherwise
     */
    public Mono<Boolean> isTokenBlacklisted(String token) {
        log.debug("Checking if token is blacklisted");

        return webClient.get()
                .uri(USER_SERVICE_LB_URL + "/api/users/blacklist/check")
                .header("X-Token-Check", token)
                .retrieve()
                .bodyToMono(Boolean.class)
                .timeout(Duration.ofSeconds(2))
                .doOnSuccess(isBlacklisted -> {
                    if (Boolean.TRUE.equals(isBlacklisted)) {
                        log.warn("Token is blacklisted");
                    } else {
                        log.debug("Token is not blacklisted");
                    }
                })
                .doOnError(error -> log.error("Failed to check token blacklist: {}", error.getMessage()))
                .onErrorReturn(false); // On error, assume not blacklisted to avoid blocking legitimate requests
    }
}

