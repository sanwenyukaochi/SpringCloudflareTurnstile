package com.salmonspark.cloudflare.turnstile.config;

import java.net.http.HttpClient;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class TurnstileServiceConfig {

    private final TurnstileConfigProperties properties;

    @Bean(name = "turnstileRestClient")
    public RestClient turnstileRestClient() {
        log.info("Creating Turnstile REST client with endpoint: {}", properties.getUrl());
        log.info(
                "Turnstile REST client timeouts - connect: {}s, read: {}s",
                properties.getConnectTimeout(),
                properties.getReadTimeout());

        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(properties.getConnectTimeout()))
                .build();

        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(Duration.ofSeconds(properties.getReadTimeout()));

        return RestClient.builder()
                .baseUrl(properties.getUrl())
                .requestFactory(requestFactory)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
