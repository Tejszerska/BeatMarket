package com.spring.beatmarket.domain.catalog.audio;

public class UnreadableAudioFileException extends RuntimeException {
    public UnreadableAudioFileException(String message) {
        super(message);
    }
}
