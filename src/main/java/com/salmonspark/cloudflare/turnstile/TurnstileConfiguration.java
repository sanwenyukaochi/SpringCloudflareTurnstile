package com.salmonspark.cloudflare.turnstile;

import com.salmonspark.cloudflare.turnstile.config.TurnstileConfigProperties;
import com.salmonspark.cloudflare.turnstile.config.TurnstileRestClientConfig;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.health.autoconfigure.contributor.ConditionalOnEnabledHealthIndicator;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(TurnstileConfigProperties.class)
@Import({TurnstileRestClientConfig.class})
public class TurnstileConfiguration {

    @Configuration
    @ConditionalOnClass(name = "io.micrometer.core.instrument.MeterRegistry")
    @Import({})
    static class TurnstileMetricsConfiguration {}

    @Configuration
    @ConditionalOnEnabledHealthIndicator("turnstile")
    @ConditionalOnClass(name = "org.springframework.boot.health.contributor.HealthIndicator")
    @Import({})
    static class TurnstileHealthConfiguration {}

    @PostConstruct
    public void onStartup() {
        log.info("SalmonSpark Spring Cloudflare Turnstile Service loaded");
    }
}
