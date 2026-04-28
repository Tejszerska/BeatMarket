package com.spring.songify.infrastructure.security.jwt;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class TokenController {
    @GetMapping("/token")
    public ResponseEntity<JwtTokenResponseDto> getToken(Authentication authentication) {
        if (authentication != null) {
            return ResponseEntity.ok(new JwtTokenResponseDto("Authenticated as: " + authentication.getName()));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();    }
}
