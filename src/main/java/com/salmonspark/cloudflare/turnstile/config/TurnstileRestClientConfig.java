package com.salmonspark.cloudflare.turnstile.config;

import com.salmonspark.cloudflare.turnstile.client.TurnstileRestClient;
import com.salmonspark.cloudflare.turnstile.service.TurnstileValidationService;
import java.net.http.HttpClient;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class TurnstileRestClientConfig {

    @Bean
    public TurnstileValidationService turnstileValidationService(
            @Qualifier("turnstileRestClient") TurnstileRestClient turnstileRestClient,
            TurnstileConfigProperties properties) {
        return new TurnstileValidationService(properties, turnstileRestClient);
    }

    @Bean(name = "turnstileRestClient")
    public TurnstileRestClient turnstileRestClient(TurnstileConfigProperties properties) {
        log.info("Creating Turnstile REST client with endpoint: {}", properties.getBaseUrl());
        log.info(
                "Turnstile REST client timeouts - connect: {}s, read: {}s",
                properties.getConnectTimeout(),
                properties.getReadTimeout());

        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(properties.getConnectTimeout()))
                .build();

        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(Duration.ofSeconds(properties.getReadTimeout()));

        RestClient restClient = RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .requestFactory(requestFactory)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .requestInterceptor((request, body, execution) -> {
                    log.debug("Turnstile API request: {} {}", request.getMethod(), request.getURI());
                    ClientHttpResponse response = execution.execute(request, body);
                    log.debug("Turnstile API response status: {}", response.getStatusCode());
                    return response;
                })
                .build();

        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory =
                HttpServiceProxyFactory.builderFor(adapter).build();

        return factory.createClient(TurnstileRestClient.class);
    }
}
