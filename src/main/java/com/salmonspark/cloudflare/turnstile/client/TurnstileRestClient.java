package com.salmonspark.cloudflare.turnstile.client;

import org.springframework.http.MediaType;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange(accept = MediaType.APPLICATION_JSON_VALUE, contentType = MediaType.APPLICATION_JSON_VALUE)
public interface TurnstileRestClient {}
