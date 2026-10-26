package com.spring.beatmarket.domain.account;

public record UserRegisteredEvent(String email, String confirmationToken) {
}
