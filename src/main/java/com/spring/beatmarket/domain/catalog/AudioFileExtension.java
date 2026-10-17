package com.spring.beatmarket.domain.catalog;

public enum AudioFileExtension {
    WAV("audio/wav"),
    FLAC("audio/flac"),
    MP3("audio/mpeg"),
    AAC("audio/aac");

    private final String contentType;

    AudioFileExtension(final String contentType) {
        this.contentType = contentType;
    }

    String contentType() {
        return contentType;
    }
}
