package br.com.fiap.ms.avaliacoes.config;

import br.com.fiap.ms.avaliacoes.exceptions.ResourceNotFoundException;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class ResilienceConfig {

    public static final String ATUALIZAR_LOCAL = "atualizarLocal";

    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> circuitBreakerCustomizer() {

        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(3)
                .minimumNumberOfCalls(2)
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofSeconds(20))
                .permittedNumberOfCallsInHalfOpenState(2)
                // 404 (local inexistente) é erro de negócio: não conta como falha do serviço
                .ignoreExceptions(ResourceNotFoundException.class)
                .build();

        TimeLimiterConfig timeLimiterConfig = TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofSeconds(5))
                .build();

        return factory -> factory.configure(builder -> builder
                .circuitBreakerConfig(circuitBreakerConfig)
                .timeLimiterConfig(timeLimiterConfig), ATUALIZAR_LOCAL);
    }
}
