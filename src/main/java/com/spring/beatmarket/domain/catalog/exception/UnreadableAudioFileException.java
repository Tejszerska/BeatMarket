package com.spring.beatmarket.domain.catalog.exception;

public class UnreadableAudioFileException extends RuntimeException {
    public UnreadableAudioFileException(String message) {
        super(message);
    }
}