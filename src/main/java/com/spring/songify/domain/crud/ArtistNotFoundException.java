package com.spring.songify.domain.crud;

class ArtistNotFoundException extends RuntimeException {
    ArtistNotFoundException(final String message) {
        super(message);
    }
}
