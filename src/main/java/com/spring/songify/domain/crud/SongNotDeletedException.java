package com.spring.songify.domain.crud;

class SongNotDeletedException extends RuntimeException {
    SongNotDeletedException(final String message) {
        super(message);
    }
}
