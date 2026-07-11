package com.spring.beatmarket.domain.crud.exception;

public class AlbumNotFoundException extends RuntimeException{
    public AlbumNotFoundException(final String message) {
        super(message);
    }
}
