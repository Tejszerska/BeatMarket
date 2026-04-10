package com.spring.songify.domain.crud.exception;

public class SongNotFoundException extends RuntimeException {
    public SongNotFoundException(String message) {
        super(message);
    }
}
