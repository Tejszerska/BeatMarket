package com.spring.beatmarket.domain.catalog.exception;

import lombok.Getter;

@Getter
public class ImageFileTooBigException extends RuntimeException {
    private final long fileSize;
    private final long maxSize;

    public ImageFileTooBigException(final long fileSize, final long maxSize) {
        super("Image file exceeds the maximum allowed size.");
        this.fileSize = fileSize;
        this.maxSize = maxSize;
    }
}
