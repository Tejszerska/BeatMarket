package com.spring.songify.domain.crud.exception;

public class ArtistNotFoundException extends RuntimeException {
    public ArtistNotFoundException(final String message) {
        super(message);
    }
}
