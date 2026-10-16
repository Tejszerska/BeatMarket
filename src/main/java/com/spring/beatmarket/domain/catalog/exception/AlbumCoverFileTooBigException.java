package com.spring.beatmarket.domain.catalog.exception;

import lombok.Getter;

@Getter
public class AlbumCoverFileTooBigException extends RuntimeException {
    private final long fileSize;
    private final long maxSize;

    public AlbumCoverFileTooBigException(final long fileSize, final long maxSize) {
        super("Album cover file exceeds the maximum allowed size.");
        this.fileSize = fileSize;
        this.maxSize = maxSize;
    }
}
