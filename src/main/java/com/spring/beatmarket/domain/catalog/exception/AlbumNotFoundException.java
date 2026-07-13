package com.spring.beatmarket.domain.catalog.exception;

public class AlbumNotFoundException extends RuntimeException{
    public AlbumNotFoundException(final String message) {
        super(message);
    }
}
