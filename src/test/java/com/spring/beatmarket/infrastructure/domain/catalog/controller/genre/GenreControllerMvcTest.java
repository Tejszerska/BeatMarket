package com.spring.beatmarket.infrastructure.domain.catalog.controller.genre;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.spring.beatmarket.domain.account.UserRepository;
import com.spring.beatmarket.domain.catalog.GenreFacade;
import com.spring.beatmarket.domain.catalog.dto.GenreDto;
import com.spring.beatmarket.infrastructure.config.WebConfig;
import com.spring.beatmarket.infrastructure.security.SecurityMvcTestConfiguration;

import jakarta.servlet.http.Cookie;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = GenreController.class
)
@Import({WebConfig.class, SecurityMvcTestConfiguration.class})
class GenreControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private KeyPair jwtTestKeyPair;

    @MockitoBean
    private GenreFacade facade;

    @MockitoBean
    private GenreControllerMapper mapper;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private AuthenticationSuccessHandler successHandler;

    @Test
    @DisplayName("Creates a genre through the MVC endpoint")
    void creates_genre_through_mvc() throws Exception {
        // given
        GenreDto.Create domainRequest = new GenreDto.Create("Synthwave");
        GenreDto.Info createdGenre = new GenreDto.Info(4L, "Synthwave");
        when(mapper.toDomainCreate(new GenreApiDto.Request("Synthwave"))).thenReturn(domainRequest);
        when(facade.addGenre(domainRequest)).thenReturn(createdGenre);
        when(mapper.toInfoResponse(createdGenre)).thenReturn(new GenreApiDto.InfoResponse(4L, "Synthwave"));

        // when
        mockMvc.perform(post("/api/catalog/genres")
                        .with(SecurityMockMvcRequestPostProcessors.user("genre-admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Synthwave"}
                                """))

                // then
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.name").value("Synthwave"));

        verify(facade).addGenre(domainRequest);
    }

    @Test
    @DisplayName("Rejects a genre request with a blank name")
    void rejects_genre_request_with_blank_name() throws Exception {
        // when
        mockMvc.perform(post("/api/catalog/genres")
                        .with(SecurityMockMvcRequestPostProcessors.user("genre-admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":" "}
                                """))

                // then
                .andExpect(status().isBadRequest());

        verify(facade, never()).addGenre(any());
    }

    @Test
    @DisplayName("Rejects genre creation by an unauthenticated user")
    void rejects_unauthenticated_genre_creation() throws Exception {
        mockMvc.perform(post("/api/catalog/genres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Synthwave"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Rejects genre creation by a customer")
    void rejects_customer_genre_creation() throws Exception {
        mockMvc.perform(post("/api/catalog/genres")
                        .with(SecurityMockMvcRequestPostProcessors.user("genre-customer").roles("CUSTOMER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Synthwave"}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Creates a genre with an administrator JWT")
    void creates_genre_with_admin_jwt() throws Exception {
        // given
        GenreDto.Create domainRequest = new GenreDto.Create("Synthwave");
        GenreDto.Info createdGenre = new GenreDto.Info(4L, "Synthwave");
        when(mapper.toDomainCreate(new GenreApiDto.Request("Synthwave"))).thenReturn(domainRequest);
        when(facade.addGenre(domainRequest)).thenReturn(createdGenre);
        when(mapper.toInfoResponse(createdGenre)).thenReturn(new GenreApiDto.InfoResponse(4L, "Synthwave"));
        String token = createToken(jwtTestKeyPair, "admin@example.com", "ROLE_ADMIN");

        // when
        mockMvc.perform(post("/api/catalog/genres")
                        .cookie(new Cookie("accessToken", token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Synthwave"}
                                """))

                // then
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.name").value("Synthwave"));

        verify(facade).addGenre(domainRequest);
    }

    @Test
    @DisplayName("Rejects genre creation with a customer JWT")
    void rejects_genre_creation_with_customer_jwt() throws Exception {
        // given
        String token = createToken(jwtTestKeyPair, "customer@example.com", "ROLE_CUSTOMER");

        // when
        mockMvc.perform(post("/api/catalog/genres")
                        .cookie(new Cookie("accessToken", token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Synthwave"}
                                """))

                // then
                .andExpect(status().isForbidden());

        verify(facade, never()).addGenre(any());
    }

    @Test
    @DisplayName("Rejects genre creation with an invalid JWT signature")
    void rejects_genre_creation_with_invalid_jwt_signature() throws Exception {
        // given
        KeyPair invalidKeyPair = createKeyPair();
        String token = createToken(invalidKeyPair, "admin@example.com", "ROLE_ADMIN");

        // when
        mockMvc.perform(post("/api/catalog/genres")
                        .cookie(new Cookie("accessToken", token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Synthwave"}
                                """))

                // then
                .andExpect(status().isUnauthorized());

        verify(facade, never()).addGenre(org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("Returns a page of genres through the MVC endpoint")
    void returns_genres_through_mvc() throws Exception {
        // given
        @SuppressWarnings("unchecked")
        Slice<GenreDto.Summary> slice = org.mockito.Mockito.mock(Slice.class);
        GenreApiDto.GetAllResponse response = new GenreApiDto.GetAllResponse(
                List.of(new GenreApiDto.SummaryResponse(4L, "Synthwave")), false);
        when(facade.findAllGenres(org.mockito.ArgumentMatchers.any(Pageable.class))).thenReturn(slice);
        when(mapper.toGetAllResponse(slice)).thenReturn(response);

        // when
        mockMvc.perform(get("/api/catalog/genres").param("page", "1").param("size", "5"))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.genres[0].id").value(4))
                .andExpect(jsonPath("$.genres[0].name").value("Synthwave"))
                .andExpect(jsonPath("$.hasNext").value(false));

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(facade).findAllGenres(pageableCaptor.capture());
        assertThat(pageableCaptor.getValue().getPageNumber()).isEqualTo(1);
        assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(5);
        assertThat(Objects.requireNonNull(pageableCaptor.getValue().getSort().getOrderFor("editedOn")).getDirection())
                .isEqualTo(Sort.Direction.ASC);
    }

    @Test
    @DisplayName("Returns genre details through the MVC endpoint")
    void returns_genre_details_through_mvc() throws Exception {
        // given
        GenreDto.Details details = new GenreDto.Details(4L, "Synthwave");
        when(facade.getGenreDetails(4L)).thenReturn(details);
        when(mapper.toDetailsResponse(details)).thenReturn(new GenreApiDto.DetailsResponse(4L, "Synthwave"));

        // when
        mockMvc.perform(get("/api/catalog/genres/4"))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.name").value("Synthwave"));
    }

    @Test
    @DisplayName("Updates a genre through the MVC endpoint")
    void updates_genre_through_mvc() throws Exception {
        // given
        GenreDto.Update domainUpdate = new GenreDto.Update("Synth pop");
        GenreDto.Info updated = new GenreDto.Info(4L, "Synth pop");
        when(mapper.toDomainUpdate(org.mockito.ArgumentMatchers.any())).thenReturn(domainUpdate);
        when(facade.updateGenre(4L, domainUpdate)).thenReturn(updated);
        when(mapper.toInfoResponse(updated)).thenReturn(new GenreApiDto.InfoResponse(4L, "Synth pop"));

        // when
        mockMvc.perform(patch("/api/catalog/genres/4")
                        .with(SecurityMockMvcRequestPostProcessors.user("genre-admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Synth pop"}
                                """))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.name").value("Synth pop"));

        verify(mapper).toDomainUpdate(new GenreApiDto.Request("Synth pop"));
        verify(facade).updateGenre(4L, domainUpdate);
    }

    @Test
    @DisplayName("Transfers songs between genres through the MVC endpoint")
    void transfers_genre_songs_through_mvc() throws Exception {
        // given
        GenreDto.Transfer transfer = new GenreDto.Transfer(3, 4L, 5L);
        when(facade.transferGenre(4L, 5L)).thenReturn(transfer);
        when(mapper.toTransferResponse(transfer)).thenReturn(new GenreApiDto.TransferResponse(3, 4L, 5L));

        // when
        mockMvc.perform(patch("/api/catalog/genres/4/transfer-to/5")
                        .with(SecurityMockMvcRequestPostProcessors.user("genre-admin").roles("ADMIN")))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.updatedSongsCount").value(3))
                .andExpect(jsonPath("$.oldGenreId").value(4))
                .andExpect(jsonPath("$.newGenreId").value(5));

        verify(facade).transferGenre(4L, 5L);
    }

    @Test
    @DisplayName("Deletes a genre through the MVC endpoint")
    void deletes_genre_through_mvc() throws Exception {
        mockMvc.perform(delete("/api/catalog/genres/4")
                        .with(SecurityMockMvcRequestPostProcessors.user("genre-admin").roles("ADMIN")))
                .andExpect(status().isNoContent());

        verify(facade).deactivateGenre(4L);
    }

    private String createToken(KeyPair keyPair, String subject, String role) {
        return JWT.create()
                .withSubject(subject)
                .withClaim("roles", List.of(role))
                .sign(Algorithm.RSA256(null, (RSAPrivateKey) keyPair.getPrivate()));
    }

    private KeyPair createKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }

}
