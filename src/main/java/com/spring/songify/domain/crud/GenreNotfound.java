package com.spring.songify.domain.crud;

class GenreNotfound extends RuntimeException {
    GenreNotfound(final String message) {
        super(message);
    }
}
