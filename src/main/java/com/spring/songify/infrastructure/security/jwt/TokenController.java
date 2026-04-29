package com.spring.songify.infrastructure.security.jwt;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RequiredArgsConstructor
@RequestMapping("/identity")
@Tag(name = "2. Authentication & Identity", description = "Endpoints for checking the current security context and user identity.")@RestController
class TokenController {
    private final JwtTokenGenerator tokenGenerator;
    @Operation(
            summary = "Get authenticated user identity",
            description = "Returns the identity of the user extracted from the JWT. By default, it returns the unique Google Subject ID or email."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Identity retrieved successfully."),
            @ApiResponse(responseCode = "401", description = "User is not authenticated (missing or invalid cookie/token).")
    })
    @GetMapping("/oauth")
    public ResponseEntity<JwtTokenResponseDto> getEmail(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            String email = jwt.getClaimAsString("email");
            return ResponseEntity.ok(new JwtTokenResponseDto("Authenticated as: " + email));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();    }

    @PostMapping("/token")
    public ResponseEntity<JwtResponseDto> authenticateAndGenerateToken(@RequestBody TokenRequestDto dto) {
        String token = tokenGenerator.authenticateAndGenerateToken(dto.username(), dto.password());
        return ResponseEntity.ok(
                JwtResponseDto.builder()
                        .token(token)
                        .build()
        );
    }
}
