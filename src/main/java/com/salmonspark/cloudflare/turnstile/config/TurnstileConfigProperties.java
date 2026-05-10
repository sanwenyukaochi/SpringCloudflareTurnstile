package com.salmonspark.cloudflare.turnstile.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Data
@Component
@PropertySource("classpath:config/turnstile.properties")
@ConfigurationProperties(prefix = "salmonspark.cloudflare.turnstile")
public class TurnstileConfigProperties {
    private String secret;
    private String siteKey;
    private String url;
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
        log.info("Turnstile URL: {}", url);
        log.info("Turnstile SiteKey: {}", siteKey);

        if (secret == null || secret.isBlank()) {
            log.error("Turnstile secret key is not configured. Validation will fail.");
        }
        if (url == null || url.isBlank()) {
            log.error("Turnstile URL is not configured. Validation will fail.");
        }
    }
}
