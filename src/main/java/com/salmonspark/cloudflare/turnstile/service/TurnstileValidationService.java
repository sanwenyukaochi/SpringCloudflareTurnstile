package com.salmonspark.cloudflare.turnstile.service;

import com.salmonspark.cloudflare.turnstile.client.TurnstileRestClient;
import com.salmonspark.cloudflare.turnstile.config.TurnstileConfigProperties;
import com.salmonspark.cloudflare.turnstile.dto.ValidateRequest;
import com.salmonspark.cloudflare.turnstile.dto.ValidateResponse;
import com.salmonspark.cloudflare.turnstile.exception.TurnstileNetworkException;
import com.salmonspark.cloudflare.turnstile.exception.TurnstileValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

@Slf4j
@RequiredArgsConstructor
public class TurnstileValidationService {

    private final TurnstileConfigProperties properties;
    private final TurnstileRestClient turnstileRestClient;

    public ValidateResponse validateTurnstileResponseDetailed(String token, String remoteIp, String idempotencyKey) {
        try {
            return turnstileRestClient.validateToken(ValidateRequest.builder()
                    .secret(properties.getSecret())
                    .response(token)
                    .remoteIp(remoteIp)
                    .idempotencyKey(idempotencyKey)
                    .build());
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
}
