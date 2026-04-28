package com.spring.songify.domain.usercrud;

import org.springframework.data.repository.Repository;

import java.util.Optional;

@org.springframework.stereotype.Repository
public interface UserRepository extends Repository<User, Long> {
    Optional<User> findFirstByEmail(String email);

    User save(User user);

    boolean existsByEmail(String email);

    Optional<User> findByConfirmationToken(String confirmationToken);
}
