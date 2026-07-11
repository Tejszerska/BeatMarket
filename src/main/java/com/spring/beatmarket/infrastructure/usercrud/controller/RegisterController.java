package com.spring.beatmarket.infrastructure.usercrud.controller;

import com.spring.beatmarket.domain.usercrud.UserConformer;
import com.spring.beatmarket.infrastructure.usercrud.controller.dto.RegisterUserRequestDto;
import com.spring.beatmarket.infrastructure.usercrud.controller.dto.RegisterUserResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "1. Registration & Activation", description = "Endpoints for creating new accounts and verifying email addresses via tokens.")
@RestController
@AllArgsConstructor
@RequestMapping("/users")
class RegisterController {
    private final UserDetailsManager userDetailsManager;
    private final UserConformer userConformer;

    @Operation(summary = "Register a new user", description = "Creates an inactive user account and triggers a confirmation email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User created successfully. Confirmation email sent."),
            @ApiResponse(responseCode = "400", description = "Invalid registration data."),
            @ApiResponse(responseCode = "409", description = "User with this email already exists.")
    })
    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponseDto> register(@RequestBody RegisterUserRequestDto request) {

        userDetailsManager.createUser(User.builder()
                .username(request.email())
                .password(request.password())
                .build());

        return ResponseEntity.ok(new RegisterUserResponseDto("User created"));
    }

    @Operation(summary = "Confirm email address", description = "Validates the token from the email link and activates the user account.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Account activated. Redirecting to Swagger UI."),
            @ApiResponse(responseCode = "404", description = "Invalid or expired token.")
    })
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
