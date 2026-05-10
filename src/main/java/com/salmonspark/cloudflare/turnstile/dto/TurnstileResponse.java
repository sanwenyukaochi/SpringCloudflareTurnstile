package com.salmonspark.cloudflare.turnstile.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Cloudflare Turnstile validate the token response.
 *
 * <p>Response fields:
 * <ul>
 *   <li><b>success</b> - Boolean indicating if validation was successful</li>
 *   <li><b>challenge_ts</b> - ISO 8601 timestamp when the challenge was solved</li>
 *   <li><b>hostname</b> - Hostname where the challenge was served</li>
 *   <li><b>error-codes</b> - Array of error codes (if validation failed)</li>
 *   <li><b>action</b> - Custom action identifier from client-side</li>
 *   <li><b>cdata</b> - Custom data payload from client-side</li>
 *   <li><b>metadata.ephemeral_id</b> - Device fingerprint ID (Enterprise only)</li>
 * </ul>
 *
 * @see <a href="https://developers.cloudflare.com/turnstile/get-started/server-side-validation/#response-fields">Cloudflare Turnstile</a>
 */
public record TurnstileResponse(
        @JsonProperty("success") Boolean success,
        @JsonProperty("challenge_ts") String challengeTs,
        @JsonProperty("hostname") String hostname,
        @JsonProperty("error-codes") List<String> errorCodes,
        @JsonProperty("action") String action,
        @JsonProperty("cdata") String cdata,
        @JsonProperty("metadata") Metadata metadata) {
    public record Metadata(@JsonProperty("ephemeral_id") String ephemeralId) {}
}
