package com.spring.songify.domain.crud.exception;

public class SongNotFoundException extends IllegalArgumentException {
    public SongNotFoundException(String message) {
        super(message);
    }
}
