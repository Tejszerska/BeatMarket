package com.spring.beatmarket.infrastructure.security;

import com.spring.beatmarket.domain.account.UserRepository;
import com.spring.beatmarket.infrastructure.config.WebConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SecurityConfigMvcTest.ProtectedEndpointController.class)
@Import({WebConfig.class, SecurityMvcTestConfiguration.class})
class SecurityConfigMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private AuthenticationSuccessHandler successHandler;

    @Test
    @DisplayName("Returns an unauthorized problem detail for a protected endpoint")
    void returns_unauthorized_problem_detail_for_protected_endpoint() throws Exception {
        // given
        String requestPath = "/api/payments";

        // when
        mockMvc.perform(get(requestPath)
                        .accept(MediaType.APPLICATION_PROBLEM_JSON))

                // then
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Authentication required"))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.detail")
                        .value("Authentication is required to access this resource."))
                .andExpect(jsonPath("$.instance").value(requestPath));
    }

    @RestController
    @RequestMapping("/api/payments")
    static class ProtectedEndpointController {
        @GetMapping
        String getPayments() {
            return "payments";
        }
    }
}
