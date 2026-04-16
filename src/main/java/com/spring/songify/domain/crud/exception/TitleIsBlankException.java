package com.spring.songify.domain.crud.exception;

public class TitleIsBlankException extends IllegalArgumentException {
    public TitleIsBlankException(String message) {
        super(message);
    }
}
