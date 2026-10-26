package com.spring.beatmarket.domain.account;

public class UserExistsException extends RuntimeException {

    public UserExistsException() {
        super("User with this email already exists.");
    }
}
