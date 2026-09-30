package com.spring.beatmarket.domain.catalog.exception;

import lombok.Getter;

@Getter
public class AudioFileTooBigException extends AudioConflictException {
    private final double fileSize;
    private final double maxSize;
    public AudioFileTooBigException(double fileSize, double maxSize) {
        super("File is too big for given format.");
        this.fileSize = fileSize;
        this.maxSize = maxSize;
    }
}
