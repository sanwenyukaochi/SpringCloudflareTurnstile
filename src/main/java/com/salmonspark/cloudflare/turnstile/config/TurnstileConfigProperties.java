package com.salmonspark.cloudflare.turnstile.config;

import lombok.Data;
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
}
