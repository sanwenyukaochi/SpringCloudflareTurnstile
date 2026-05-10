package com.salmonspark.cloudflare.turnstile.service;

import com.salmonspark.cloudflare.turnstile.client.TurnstileRestClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class TurnstileValidationService {
    private static final String UNKNOWN = "unknown";
    private static final int MIN_TOKEN_LENGTH = 20;

    private final TurnstileRestClient turnstileRestClient;
}
