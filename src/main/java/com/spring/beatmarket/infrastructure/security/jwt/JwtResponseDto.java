package com.spring.beatmarket.infrastructure.security.jwt;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "Response containing the generated JWT token")
public record JwtResponseDto(
        @Schema(description = "The JWT access token. Returned in the response body for testing, but primarily set as an HttpOnly secure cookie.",
                example = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...")
        String token
) {}