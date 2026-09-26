package com.spring.beatmarket.domain.catalog.audio;

public class IncorrectAudioFormat extends RuntimeException {
    public IncorrectAudioFormat(String message) {
        super(message);
    }
}
