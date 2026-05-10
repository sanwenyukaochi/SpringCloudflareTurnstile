package com.salmonspark.cloudflare.turnstile.exception;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.Getter;

public class TurnstileValidationException extends TurnstileException {

    @Getter
    private final List<String> errorCodes;

    public TurnstileValidationException(String message, List<String> errorCodes) {
        super(message);
        this.errorCodes = Collections.unmodifiableList(Objects.requireNonNull(errorCodes));
    }
}
