package com.salmonspark.cloudflare.turnstile.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.NonNull;
/**
 * Cloudflare Turnstile validate the token request.
 *
 *
 * @see <a href="https://developers.cloudflare.com/turnstile/get-started/server-side-validation/#siteverify-api-overview">Cloudflare Turnstile</a>
 */
public record ValidateRequest(
        @NonNull
        @JsonProperty("secret") String secret,
        @NonNull
        @JsonProperty("response") String response,
        @JsonProperty("remoteip") String remoteIp,
        @JsonProperty("idempotency_key") String idempotencyKey
) {
}
