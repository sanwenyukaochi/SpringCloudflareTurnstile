package com.salmonspark.cloudflare.turnstile.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

@Data
@PropertySource("classpath:config/turnstile.properties")
@ConfigurationProperties(prefix = "salmon-spark.cloudflare.turnstile")
public class TurnstileConfigProperties {
    private String secret;
    private String siteKey;
    private String baseUrl = "https://challenges.cloudflare.com";
    private int connectTimeout = 5;
    private int readTimeout = 10;
    private Metrics metrics = new Metrics();

    @Data
    public static class Metrics {
        private boolean enabled = true;
        private boolean healthCheckEnabled = true;
        private int errorThreshold = 10;
    }

    private static final Logger log = LoggerFactory.getLogger(TurnstileConfigProperties.class);

    @PostConstruct
    public void onStartup() {
        log.info("TurnstileValidationService started");
        log.info("Turnstile Base URL: {}", baseUrl);
        log.info("Turnstile SiteKey: {}", siteKey);

        if (secret == null || secret.isBlank()) {
            log.error("Turnstile secret key is not configured. Validation will fail.");
        }
        if (baseUrl == null || baseUrl.isBlank()) {
            log.error("Turnstile Base URL is not configured. Validation will fail.");
        }
    }
}
