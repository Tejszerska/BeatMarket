package com.spring.beatmarket.domain.crud.exception;

public class GenreNotfoundException extends RuntimeException {
    public GenreNotfoundException(final String message) {
        super(message);
    }
}
