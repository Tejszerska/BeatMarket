package com.spring.songify.infrastructure.usercrud.controller;

import com.spring.songify.domain.usercrud.UserConformer;
import com.spring.songify.infrastructure.usercrud.controller.dto.RegisterUserRequestDto;
import com.spring.songify.infrastructure.usercrud.controller.dto.RegisterUserResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
class RegisterController {
    private final UserDetailsManager userDetailsManager;
    private final UserConformer userConformer;

    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponseDto> register(@RequestBody RegisterUserRequestDto request) {

        userDetailsManager.createUser(User.builder()
                .username(request.email())
                .password(request.password())
                .build());

        return ResponseEntity.ok(new RegisterUserResponseDto("User created"));
    }

    @GetMapping("/confirm")
    public ResponseEntity<Object> confirm(@RequestParam String token) {
        boolean isConfirmed = userConformer.confirmUser(token);
        if (isConfirmed) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create("https://localhost:8443/swagger-ui/index.html"))
                    .build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Confirmation failed. User can't login");
        }
    }
}
