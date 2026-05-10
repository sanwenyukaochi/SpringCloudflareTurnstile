package com.salmonspark.cloudflare.turnstile.service;

import com.salmonspark.cloudflare.turnstile.config.TurnstileConfigProperties;
import com.salmonspark.cloudflare.turnstile.exception.TurnstileConfigurationException;
import com.salmonspark.cloudflare.turnstile.exception.TurnstileNetworkException;
import com.salmonspark.cloudflare.turnstile.exception.TurnstileValidationException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

@Slf4j
public class TMP {
    private static final String UNKNOWN = "unknown";
    private static final int MIN_TOKEN_LENGTH = 20;

    private final RestClient turnstileRestClient;
    private final TurnstileConfigProperties properties;

    public TMP(
            @Qualifier("turnstileRestClient") RestClient turnstileRestClient, TurnstileConfigProperties properties) {
        this.turnstileRestClient = turnstileRestClient;
        this.properties = properties;
    }

    @PostConstruct
    public void onStartup() {
        log.info("TurnstileValidationService started");
        log.info("Turnstile URL: {}", properties.getUrl());
        log.info("Turnstile SiteKey: {}", properties.getSiteKey());

        if (properties.getSecret() == null || properties.getSecret().isBlank()) {
            log.error("Turnstile secret key is not configured. Validation will fail.");
        }
        if (properties.getUrl() == null || properties.getUrl().isBlank()) {
            log.error("Turnstile URL is not configured. Validation will fail.");
        }
    }

    public boolean validateTurnstileResponse(String token) {
        return validateTurnstileResponse(token, null);
    }

    public boolean validateTurnstileResponse(String token, String remoteIp) {
        try {
            ValidationResult result = validateTurnstileResponseDetailed(token, remoteIp);
            return result.isSuccess();
        } catch (Exception e) {
            log.error("Unexpected error during Turnstile validation: {}", e.getMessage(), e);
            return false;
        }
    }

    public ValidationResult validateTurnstileResponseDetailed(String token) {
        return validateTurnstileResponseDetailed(token, null);
    }

    public ValidationResult validateTurnstileResponseDetailed(String token, String remoteIp) {
        log.trace("Starting validation for token: {} with remoteIp: {}", token, remoteIp);

        // 参数校验
        if (token == null) {
            log.warn("Turnstile validation failed: token cannot be null");
            return ValidationResult.inputError("Token cannot be null");
        }

        if (token.isEmpty() || token.isBlank()) {
            log.warn("Turnstile validation failed: token cannot be empty or blank");
            return ValidationResult.inputError("Token cannot be empty or blank");
        }

        if (token.length() < MIN_TOKEN_LENGTH) {
            log.warn(
                    "Turnstile validation failed: token appears to be too short to be valid (length: {})",
                    token.length());
            return ValidationResult.inputError("Token is too short to be valid (length: " + token.length() + ")");
        }

        String cleanRemoteIp = remoteIp;
        if (cleanRemoteIp != null && (cleanRemoteIp.isBlank())) {
            log.warn("Turnstile validation: ignoring empty or blank remoteIp");
            cleanRemoteIp = null;
        }

        // 配置检查
        if (properties.getSecret() == null || properties.getSecret().isBlank()) {
            String msg = "Turnstile secret key is not configured";
            log.error(msg);
            throw new TurnstileConfigurationException(msg);
        }

        if (properties.getUrl() == null || properties.getUrl().isBlank()) {
            String msg = "Turnstile URL is not configured";
            log.error(msg);
            throw new TurnstileConfigurationException(msg);
        }

        // 构建请求体
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("secret", properties.getSecret());
        requestBody.put("response", token);
        Optional.ofNullable(cleanRemoteIp).ifPresent(ip -> requestBody.put("remoteip", ip));

        log.trace("Making request to Cloudflare Turnstile API at: {}", properties.getUrl());

        try {
            return executeValidationRequest(requestBody);
        } catch (HttpClientErrorException e) {
            log.error("Client error during Turnstile validation: {}", e.getMessage(), e);
            throw new TurnstileNetworkException("Client error: " + e.getMessage(), e);
        } catch (HttpServerErrorException e) {
            log.error("Server error during Turnstile validation: {}", e.getMessage(), e);
            throw new TurnstileNetworkException("Server error: " + e.getMessage(), e);
        } catch (ResourceAccessException e) {
            log.error("Network error during Turnstile validation: {}", e.getMessage(), e);
            throw new TurnstileNetworkException("Network error: " + e.getMessage(), e);
        } catch (TurnstileValidationException e) {
            log.debug("Turnstile token rejected by Cloudflare: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error(
                    "Unexpected {} during Turnstile validation: {}",
                    e.getClass().getSimpleName(),
                    e.getMessage(),
                    e);
            throw new TurnstileNetworkException("Unexpected error: " + e.getMessage(), e);
        }
    }

    private ValidationResult executeValidationRequest(Map<String, String> requestBody) {
        TurnstileResponse response = turnstileRestClient
                .post()
                .uri(properties.getUrl())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(requestBody)
                .retrieve()
                .body(TurnstileResponse.class);

        log.debug("Turnstile response: {}", response);

        if (response == null) {
            log.warn("Turnstile API returned null response");
            return ValidationResult.networkError("Cloudflare returned an empty response");
        }

        if (response.isSuccess()) {
            log.debug("Turnstile validation successful");
            return ValidationResult.success();
        } else {
            log.warn("Turnstile validation failed with error codes: {}", response.getErrorCodes());
            throw new TurnstileValidationException("Token validation failed", response.getErrorCodes());
        }
    }

    public String getClientIpAddress(ServletRequest request) {
        if (request instanceof HttpServletRequest httpRequest) {
            String[] headers = {
                "X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"
            };
            for (String header : headers) {
                String ipHeaderValue = httpRequest.getHeader(header);
                if (ipHeaderValue == null || ipHeaderValue.isBlank()) {
                    continue;
                }
                String candidate = ipHeaderValue.split(",", 2)[0].trim();
                if (!candidate.isEmpty() && !UNKNOWN.equalsIgnoreCase(candidate)) {
                    return candidate;
                }
            }
        }
        return request.getRemoteAddr();
    }
}
