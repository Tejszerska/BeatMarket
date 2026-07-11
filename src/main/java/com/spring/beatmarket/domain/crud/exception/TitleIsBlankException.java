package com.spring.beatmarket.domain.crud.exception;

public class TitleIsBlankException extends IllegalArgumentException {
    public TitleIsBlankException(String message) {
        super(message);
    }
}
