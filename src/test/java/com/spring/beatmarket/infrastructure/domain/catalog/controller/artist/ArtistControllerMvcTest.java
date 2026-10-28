package com.spring.beatmarket.infrastructure.domain.catalog.controller.artist;

import com.spring.beatmarket.domain.account.UserRepository;
import com.spring.beatmarket.domain.catalog.ArtistFacade;
import com.spring.beatmarket.domain.catalog.dto.ArtistDto;
import com.spring.beatmarket.domain.catalog.dto.ImageDto;
import com.spring.beatmarket.infrastructure.config.JacksonConfig;
import com.spring.beatmarket.infrastructure.config.WebConfig;
import com.spring.beatmarket.infrastructure.security.SecurityMvcTestConfiguration;
import com.spring.beatmarket.infrastructure.upload.UploadFileValidator;

import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ArtistController.class)
@Import({WebConfig.class, JacksonConfig.class, SecurityMvcTestConfiguration.class, UploadFileValidator.class})
class ArtistControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ArtistFacade facade;

    @MockitoBean
    private ArtistControllerMapper mapper;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private AuthenticationSuccessHandler successHandler;

    @Test
    @DisplayName("Returns an unauthorized problem detail for an unauthenticated request")
    void returns_unauthorized_problem_detail() throws Exception {
        mockMvc.perform(post("/api/catalog/artists"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Authentication required"))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.detail").value("Authentication is required to access this resource."))
                .andExpect(jsonPath("$.instance").value("/api/catalog/artists"));
    }

    @Test
    @DisplayName("Returns artists through the public MVC endpoint")
    void returns_artists_through_mvc() throws Exception {
        // given
        @SuppressWarnings("unchecked")
        Slice<ArtistDto.Summary> slice = mock(Slice.class);
        ArtistApiDto.GetAllResponse response = new ArtistApiDto.GetAllResponse(
                List.of(new ArtistApiDto.SummaryResponse(42L, "Artist", null)), false);
        when(facade.findAllArtists(eq("Artist"), any(Pageable.class))).thenReturn(slice);
        when(mapper.toGetAllResponse(slice)).thenReturn(response);

        // when
        mockMvc.perform(get("/api/catalog/artists").param("name", "Artist"))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.artists[0].id").value(42))
                .andExpect(jsonPath("$.artists[0].name").value("Artist"));

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(facade).findAllArtists(eq("Artist"), pageableCaptor.capture());
        assertThat(pageableCaptor.getValue().getPageNumber()).isZero();
        assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(20);
        assertThat(Objects.requireNonNull(pageableCaptor.getValue().getSort().getOrderFor("editedOn")).getDirection())
                .isEqualTo(Sort.Direction.ASC);
    }

    @Test
    @DisplayName("Returns artist details through the MVC endpoint")
    void returns_artist_details_through_mvc() throws Exception {
        // given
        ArtistDto.Details details = new ArtistDto.Details(42L, "Artist", null, List.of(), List.of());
        when(facade.getArtistDetails(42L)).thenReturn(details);
        when(mapper.toDetailsResponse(details))
                .thenReturn(new ArtistApiDto.DetailsResponse(42L, "Artist", null, List.of(), List.of()));

        // when
        mockMvc.perform(get("/api/catalog/artists/42"))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.name").value("Artist"));
    }

    @Test
    @DisplayName("Creates an artist through the MVC endpoint")
    void creates_artist_through_mvc() throws Exception {
        // given
        ArtistDto.Create create = new ArtistDto.Create("Artist", List.of(), List.of(), List.of(), List.of());
        ArtistDto.Info created = new ArtistDto.Info(42L, "Artist", null, List.of(), List.of());
        when(mapper.toDomainCreate(any())).thenReturn(create);
        when(facade.addArtist(create)).thenReturn(created);
        when(mapper.toInfoResponse(created))
                .thenReturn(new ArtistApiDto.InfoResponse(42L, "Artist", null, List.of(), List.of()));

        // when
        mockMvc.perform(post("/api/catalog/artists")
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Artist","mainSongIds":[],"featSongIds":[],"mainAlbumIds":[],"featAlbumIds":[]}
                                """))

                // then
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.name").value("Artist"));

        verify(mapper).toDomainCreate(new ArtistApiDto.CreateRequest(
                "Artist", List.of(), List.of(), List.of(), List.of()));
        verify(facade).addArtist(create);
    }

    @Test
    @DisplayName("Rejects an artist request without a name")
    void rejects_artist_without_name() throws Exception {
        mockMvc.perform(post("/api/catalog/artists")
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":" "}
                                """))
                .andExpect(status().isBadRequest());

        verify(facade, never()).addArtist(any());
    }

    @Test
    @DisplayName("Updates an artist through the MVC endpoint")
    void updates_artist_through_mvc() throws Exception {
        // given
        ArtistDto.Update update = new ArtistDto.Update(
                java.util.Optional.of("Updated artist"), java.util.Optional.empty(), java.util.Optional.empty(),
                java.util.Optional.empty(), java.util.Optional.empty());
        ArtistDto.Info updated = new ArtistDto.Info(42L, "Updated artist", null, List.of(), List.of());
        when(mapper.toDomainUpdate(any())).thenReturn(update);
        when(facade.updateArtist(42L, update)).thenReturn(updated);
        when(mapper.toInfoResponse(updated))
                .thenReturn(new ArtistApiDto.InfoResponse(42L, "Updated artist", null, List.of(), List.of()));

        // when
        mockMvc.perform(patch("/api/catalog/artists/42")
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Updated artist"}
                                """))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.name").value("Updated artist"));

        ArgumentCaptor<ArtistApiDto.UpdateRequest> requestCaptor =
                ArgumentCaptor.forClass(ArtistApiDto.UpdateRequest.class);
        verify(mapper).toDomainUpdate(requestCaptor.capture());
        assertThat(requestCaptor.getValue().name().get()).isEqualTo("Updated artist");
        verify(facade).updateArtist(42L, update);
    }

    @Test
    @DisplayName("Uploads an artist image through the MVC endpoint")
    void uploads_artist_image_through_mvc() throws Exception {
        // given
        byte[] imageBytes = {1, 2, 3};
        MockMultipartFile file = new MockMultipartFile("file", "artist.png", "image/png", imageBytes);
        ImageDto.Upload uploaded = new ImageDto.Upload(42L, "Image uploaded", "https://cdn/artist.webp");
        when(facade.updateArtistImage(42L, imageBytes)).thenReturn(uploaded);
        when(mapper.toImageResponse(uploaded))
                .thenReturn(new ArtistApiDto.ImageResponse(42L, "Image uploaded", "https://cdn/artist.webp"));

        // when
        mockMvc.perform(multipart("/api/catalog/artists/42/image")
                        .file(file)
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.imageUrl").value("https://cdn/artist.webp"));
    }

    @Test
    @DisplayName("Rejects an unsupported artist image through the MVC endpoint")
    void rejects_unsupported_artist_image_through_mvc() throws Exception {
        mockMvc.perform(multipart("/api/catalog/artists/42/image")
                        .file(new MockMultipartFile("file", "artist.gif", "image/gif", new byte[]{1}))
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid upload file"));

        verify(facade, never()).updateArtistImage(any(Long.class), any(byte[].class));
    }

    @Test
    @DisplayName("Rejects an empty artist image through the MVC endpoint")
    void rejects_empty_artist_image_through_mvc() throws Exception {
        mockMvc.perform(multipart("/api/catalog/artists/42/image")
                        .file(new MockMultipartFile("file", "artist.png", "image/png", new byte[0]))
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Uploaded file must not be empty."));

        verify(facade, never()).updateArtistImage(any(Long.class), any(byte[].class));
    }

    @Test
    @DisplayName("Deletes an artist through the MVC endpoint")
    void deletes_artist_through_mvc() throws Exception {
        mockMvc.perform(delete("/api/catalog/artists/42")
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isNoContent());

        verify(facade).deactivateArtist(42L);
    }
}
