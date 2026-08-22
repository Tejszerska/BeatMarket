package com.spring.beatmarket.domain.catalog.exception;

public class DataConflictException  extends RuntimeException {
    public DataConflictException(final String message) {
        super(message);
    }
}
