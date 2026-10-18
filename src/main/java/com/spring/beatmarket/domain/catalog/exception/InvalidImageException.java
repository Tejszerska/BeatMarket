package com.spring.beatmarket.domain.catalog.exception;

public class InvalidImageException extends RuntimeException {
    public InvalidImageException(final String message) {
        super(message);
    }
}
