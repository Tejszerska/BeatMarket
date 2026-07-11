package com.spring.beatmarket.domain.crud.exception;

public class NameIsBlankException extends RuntimeException {
    public NameIsBlankException(String message) {
        super(message);
    }
}
