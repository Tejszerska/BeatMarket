package com.spring.beatmarket.domain.catalog.exception;

public class GenreNotfoundException extends RuntimeException {
    public GenreNotfoundException(final String message) {
        super(message);
    }
}
