package com.salmonspark.cloudflare.turnstile.client;

import com.salmonspark.cloudflare.turnstile.dto.ValidateRequest;
import com.salmonspark.cloudflare.turnstile.dto.ValidateResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange(accept = MediaType.APPLICATION_JSON_VALUE, contentType = MediaType.APPLICATION_JSON_VALUE)
public interface TurnstileRestClient {

    @PostExchange("/turnstile/v0/siteverify")
    ValidateResponse validateToken(@RequestBody ValidateRequest validateRequest);
}
