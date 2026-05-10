package com.salmonspark.cloudflare.turnstile.exception;

public class TurnstileNetworkException extends TurnstileException {

    public TurnstileNetworkException(String message) {
        super(message);
    }

    public TurnstileNetworkException(String message, Throwable cause) {
        super(message, cause);
    }
}
