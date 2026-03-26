package com.spring.songify.domain.crud;

class GenreNotDeletedException extends RuntimeException {
    GenreNotDeletedException(final String message) {
        super(message);
    }
}
