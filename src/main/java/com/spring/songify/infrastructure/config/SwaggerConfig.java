package com.spring.songify.infrastructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Songify API",
                version = "1.0",
                description = """
                        API for managing a music database.
                        
                        ### 🔒 Security Architecture (Hybrid)
                        The application features an advanced authentication system combining traditional registration with Social Login:
                        * **Google Login (OAuth2):** Navigate to `/oauth2/authorization/google`. An account is automatically created upon the first login (Stateless).
                        * **Manual Registration:** Available at `/users/register`. Requires account activation via an email confirmation link.
                        
                        **Important Technical Note:**\s
                        The API utilizes `HttpOnly Secure` cookies. After a successful Google login, the JWT is stored in a cookie and automatically attached to every subsequent request by the browser. **You do not need to manually copy the token into the 'Authorize' lock.**"""
        ),
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
class SwaggerConfig {

}