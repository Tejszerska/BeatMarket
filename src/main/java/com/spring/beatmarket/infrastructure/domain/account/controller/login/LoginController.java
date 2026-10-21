package com.spring.beatmarket.infrastructure.domain.account.controller.login;

import com.spring.beatmarket.infrastructure.security.jwt.CookieService;
import com.spring.beatmarket.infrastructure.security.jwt.dto.JwtGenerationDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "1.  Account Module", description = "Endpoints for handling user identity, security, and access control.")
@RestController
class LoginController {

    private final CookieService cookieService;
    private final LoginControllerMapper mapper;

    @Operation(
            summary = "Authenticate user and generate token",
            description = "Authenticates a user and issues an HttpOnly JWT cookie for subsequent requests."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully authenticated. Sets an `HttpOnly` authorization cookie in the response headers."),
            @ApiResponse(responseCode = "401", description = "Authentication failed due to incorrect credentials or an unverified account.")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticateAndGenerateToken(@Valid @RequestBody LoginRequest dto, HttpServletResponse response) {
        JwtGenerationDto jwtGenerationDto = mapper.mapFromLoginRequestToJwtGenerationDto(dto);
        Cookie cookie = cookieService.createAccessTokenCookie(jwtGenerationDto, 360);
        response.addCookie(cookie);
        return ResponseEntity.ok(new LoginResponse("Login successful."));
    }

    @Operation(
            summary = "Clear authorization.",
            description = "Clears the JWT authorization cookie, effectively logging the user out"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully cleared the JWT authorization cookie,."),
    })
    @PostMapping("/logout")
    public ResponseEntity<LoginResponse> clearJwtAuthorizationCookie(HttpServletResponse response) {
        Cookie cookie = cookieService.clearTokenCookie();
        response.addCookie(cookie);
        return ResponseEntity.ok(new LoginResponse("Logged out successfully."));
    }

}