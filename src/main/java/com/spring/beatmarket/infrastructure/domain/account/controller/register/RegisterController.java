package com.spring.beatmarket.infrastructure.domain.account.controller.register;

import com.spring.beatmarket.domain.account.UserExistsException;
import com.spring.beatmarket.domain.account.UserFacade;
import com.spring.beatmarket.infrastructure.domain.account.controller.register.dto.RegisterUserRequestDto;
import com.spring.beatmarket.infrastructure.domain.account.controller.register.dto.RegisterUserResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@Tag(name = "1.  Account Module", description = "Endpoints for handling user identity, security, and access control.")
@RestController
@AllArgsConstructor
@RequestMapping("/users")
class RegisterController {

    private final UserFacade userFacade;
    private final RegisterControllerMapper mapper;

    @Operation(summary = "Register a new user", description = "Creates an inactive user account and triggers a confirmation email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully. Confirmation email sent."),
            @ApiResponse(responseCode = "400", description = "Invalid registration data."),
            @ApiResponse(responseCode = "409", description = "User with this email already exists.")
    })
    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponseDto> register(@Valid @RequestBody RegisterUserRequestDto request) {
        try {
            userFacade.registerNewUser(mapper.mapFromRegisterUserRequestDtoToUserRequestDto(request));
        } catch (UserExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new RegisterUserResponseDto("User with this email already exists."));
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RegisterUserResponseDto("User created successfully. Confirmation email sent."));
    }

    @Operation(summary = "Confirm email address", description = "Validates the token from the email link and activates the user account.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Account activated. Redirecting to Swagger UI."),
            @ApiResponse(responseCode = "404", description = "Failed token.")
    })
    @GetMapping("/confirm")
    public ResponseEntity<RegisterUserResponseDto> confirm(@RequestParam String token) {
        boolean isConfirmed = userFacade.confirmUser(token);
        if (isConfirmed) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create("https://localhost:8443/swagger-ui/index.html"))
                    .body((new RegisterUserResponseDto("Account activated. Redirecting to Swagger UI")));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body((new RegisterUserResponseDto("Confirmation failed. cannot login")));
        }
    }
}
