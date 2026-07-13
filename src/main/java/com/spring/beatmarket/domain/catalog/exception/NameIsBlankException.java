package com.spring.beatmarket.domain.catalog.exception;

public class NameIsBlankException extends RuntimeException {
    public NameIsBlankException(String message) {
        super(message);
    }
}
