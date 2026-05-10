package com.salmonspark.cloudflare.turnstile.exception;

public class TurnstileException extends RuntimeException {

    public TurnstileException(String message) {
        super(message);
    }

    public TurnstileException(String message, Throwable cause) {
        super(message, cause);
    }
}
