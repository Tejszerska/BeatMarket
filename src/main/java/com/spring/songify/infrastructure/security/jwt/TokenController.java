package com.spring.songify.infrastructure.security.jwt;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/identity")
@Tag(name = "2. Authentication & Identity", description = "Endpoints for managing traditional login and retrieving the current security context.")
@RestController
class TokenController {

    private final JwtTokenGenerator tokenGenerator;
    private final CookieService cookieService;

    @Operation(
            summary = "Get authenticated user identity",
            description = "Retrieves the identity (email) of the currently authenticated user from the active session or JWT token."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Identity retrieved successfully."),
            @ApiResponse(responseCode = "401", description = "User is not authenticated (missing, invalid, or expired token/cookie).")
    })
    @GetMapping("/email")
    public ResponseEntity<GetEmailDto> getEmail(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            return ResponseEntity.ok(new GetEmailDto(email));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @Operation(
            summary = "Authenticate user and generate token",
            description = "Verifies user credentials against the database. If successful, generates an RSA-signed JWT and sets it as an HttpOnly secure cookie named `accessToken`."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully authenticated. Cookie is set, and token is returned in the response body."),
            @ApiResponse(responseCode = "401", description = "Authentication failed due to incorrect credentials or an unverified account.")
    })
    @PostMapping("/token")
    public ResponseEntity<JwtResponseDto> authenticateAndGenerateToken(@RequestBody TokenRequestDto dto, HttpServletResponse response) {
        String token = tokenGenerator.authenticateAndGenerateToken(dto.username(), dto.password());
        Cookie cookie = cookieService.createAccessTokenCookie(token, 360);
        response.addCookie(cookie);

        return ResponseEntity.ok(new JwtResponseDto(token));
    }
}