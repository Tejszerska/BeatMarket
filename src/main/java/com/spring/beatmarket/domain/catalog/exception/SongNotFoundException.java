package com.spring.beatmarket.domain.catalog.exception;

public class SongNotFoundException extends IllegalArgumentException {
    public SongNotFoundException(String message) {
        super(message);
    }
}
