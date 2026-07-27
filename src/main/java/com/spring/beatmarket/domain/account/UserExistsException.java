package com.spring.beatmarket.domain.account;

public class UserExistsException extends RuntimeException {

    UserExistsException(final String message) {
        super(message);
    }
}
