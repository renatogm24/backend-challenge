package com.clara.backend_challenge.core.exceptions;

public class DiscogsApiException extends RuntimeException {
    public DiscogsApiException(String message, Throwable cause) {
        super(message, cause);
    }
}