package com.spring.beatmarket.domain.catalog.exception;

public class InvalidAlbumCoverException extends RuntimeException {
    public InvalidAlbumCoverException(final String message) {
        super(message);
    }
}
