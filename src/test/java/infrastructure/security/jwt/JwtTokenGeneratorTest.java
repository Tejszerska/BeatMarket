package infrastructure.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.spring.songify.domain.usercrud.User;
import com.spring.songify.infrastructure.security.SecurityUser;
import com.spring.songify.infrastructure.security.jwt.JwtConfigurationProperties;
import com.spring.songify.infrastructure.security.jwt.JwtTokenGenerator;
import jdk.jfr.Description;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtTokenGeneratorTest {
    @Mock
    UserDetailsService userDetailsService;

    private JwtTokenGenerator jwtTokenGenerator;

    @BeforeEach
    void setUp() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair testKeyPair = keyPairGenerator.generateKeyPair();

        Clock fixedClock = Clock.fixed(Instant.parse("2024-01-01T12:00:00Z"), ZoneId.of("UTC"));
        JwtConfigurationProperties properties = new JwtConfigurationProperties(10, "SongifyApp");

        jwtTokenGenerator = new JwtTokenGenerator(
                null,
                fixedClock,
                properties,
                testKeyPair,
                userDetailsService
        );
    }

    @Test
    @Description(value = "Should genrate valid JWT token for Google Login OAuth2 user")
    void shouldGenerateValidJwtTokenForOAuthUser() {
        // given
        String email = "test@gmail.com";
        User dbUser = User.builder()
                .email(email)
                .authorities(List.of("ROLE_USER"))
                .build() ;
        SecurityUser securityUser = new SecurityUser(dbUser);

        when(userDetailsService.loadUserByUsername(email)).thenReturn(securityUser);

        // when
        String token = jwtTokenGenerator.generateTokenForOAuthUser(email);

        //then
        DecodedJWT decodedJWT = JWT.decode(token);

        assertThat(decodedJWT.getSubject()).isEqualTo("test@gmail.com");
        assertThat(decodedJWT.getIssuer()).isEqualTo("SongifyApp");
        assertThat(decodedJWT.getClaim("roles").asList(String.class)).containsExactly("ROLE_USER");
        assertThat(decodedJWT.getExpiresAtAsInstant()).isEqualTo(Instant.parse("2024-01-01T12:10:00Z"));
    }
}
