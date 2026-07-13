package com.spring.beatmarket.domain.catalog.exception;

public class TitleIsBlankException extends IllegalArgumentException {
    public TitleIsBlankException(String message) {
        super(message);
    }
}
