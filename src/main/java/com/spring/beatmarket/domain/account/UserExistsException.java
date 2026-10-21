package com.spring.beatmarket.domain.account;

public class UserExistsException extends RuntimeException {

    public UserExistsException(final String message) {
        super(message);
    }
}
