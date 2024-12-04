package com.clara.backend_challenge.core.exceptions;

public class ArtistNotFoundException extends RuntimeException {
    public ArtistNotFoundException(String message) {
        super(message);
    }
}
