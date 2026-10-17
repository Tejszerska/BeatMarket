package com.spring.beatmarket.infrastructure.upload;

public class InvalidUploadFileException extends RuntimeException {
    public InvalidUploadFileException(final String message) {
        super(message);
    }
}
