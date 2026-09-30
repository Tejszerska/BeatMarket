package com.spring.beatmarket.domain.catalog.exception;

public class IncorrectAudioFormatException extends AudioConflictException {
    public IncorrectAudioFormatException(String message) {
        super(message);
    }
}
