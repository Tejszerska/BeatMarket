package com.spring.beatmarket.infrastructure.upload;

import lombok.Getter;

@Getter
public class UploadFileTooBigException extends RuntimeException {
    private final long fileSize;
    private final long maxSize;

    public UploadFileTooBigException(final long fileSize, final long maxSize) {
        super("Uploaded file exceeds the maximum allowed size.");
        this.fileSize = fileSize;
        this.maxSize = maxSize;
    }

}
