package com.spring.beatmarket.infrastructure.domain.catalog.controller.song;

import com.spring.beatmarket.domain.account.UserRepository;
import com.spring.beatmarket.domain.catalog.SongFacade;
import com.spring.beatmarket.domain.catalog.SongLanguage;
import com.spring.beatmarket.domain.catalog.dto.SongDto;
import com.spring.beatmarket.infrastructure.config.JacksonConfig;
import com.spring.beatmarket.infrastructure.config.WebConfig;
import com.spring.beatmarket.infrastructure.security.SecurityMvcTestConfiguration;
import com.spring.beatmarket.infrastructure.upload.UploadFileValidator;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SongController.class)
@Import({WebConfig.class, JacksonConfig.class, SecurityMvcTestConfiguration.class, UploadFileValidator.class})
class SongControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SongFacade facade;

    @MockitoBean
    private SongControllerMapper mapper;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private AuthenticationSuccessHandler successHandler;

    @Test
    @DisplayName("Returns songs through the public MVC endpoint")
    void returns_songs_through_mvc() throws Exception {
        // given
        SongDto.SearchCriteria criteria = new SongDto.SearchCriteria(
                null, null, null, null, null, null, null, null, null, null);
        @SuppressWarnings("unchecked")
        Slice<SongDto.Summary> slice = mock(Slice.class);
        when(mapper.toDomainSearchCriteria(any())).thenReturn(criteria);
        when(facade.searchSongs(eq(criteria), any(Pageable.class))).thenReturn(slice);
        when(mapper.toGetAllResponse(slice)).thenReturn(new SongApiDto.GetAllResponse(List.of(), false));

        // when
        mockMvc.perform(get("/api/catalog/songs")
                        .param("maxPrice", "10.00")
                        .param("currency", "USD")
                        .param("license", "STANDARD"))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.songs").isEmpty())
                .andExpect(jsonPath("$.hasNext").value(false));

        ArgumentCaptor<SongApiDto.SearchRequest> requestCaptor =
                ArgumentCaptor.forClass(SongApiDto.SearchRequest.class);
        verify(mapper).toDomainSearchCriteria(requestCaptor.capture());
        assertThat(requestCaptor.getValue().maxPrice())
                .isEqualByComparingTo("10.00");
        assertThat(requestCaptor.getValue().currency()).isEqualTo("USD");
        assertThat(requestCaptor.getValue().license()).isEqualTo("STANDARD");

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(facade).searchSongs(eq(criteria), pageableCaptor.capture());
        assertThat(pageableCaptor.getValue().getPageNumber()).isZero();
        assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(20);
        assertThat(Objects.requireNonNull(pageableCaptor.getValue().getSort().getOrderFor("editedOn")).getDirection())
                .isEqualTo(Sort.Direction.DESC);
    }

    @ParameterizedTest
    @CsvSource({"USD,", ",STANDARD"})
    @DisplayName("Rejects a song price filter without currency or license")
    void rejects_incomplete_price_filter(String currency, String license) throws Exception {
        MockHttpServletRequestBuilder request = get("/api/catalog/songs").param("maxPrice", "10");
        if (currency != null) {
            request.param("currency", currency);
        }
        if (license != null) {
            request.param("license", license);
        }

        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.maxPrice")
                        .value("Filtering by maxPrice requires declaring currency and license."));

        verifyNoInteractions(facade, mapper);
    }

    @Test
    @DisplayName("Returns song details to an authenticated customer")
    void returns_song_details_to_customer() throws Exception {
        // given
        SongDto.Details details = new SongDto.Details(
                12L, "Song", "EN", 180, LocalDate.of(2020, 1, 1),
                null, List.of(), null, null, Map.of());
        SongApiDto.DetailsResponse response = new SongApiDto.DetailsResponse(
                12L, "Song", "EN", 180, LocalDate.of(2020, 1, 1),
                null, List.of(), null, null, Map.of());
        when(facade.getSongDetails(12L)).thenReturn(details);
        when(mapper.toDetailsResponse(details)).thenReturn(response);

        // when
        mockMvc.perform(get("/api/catalog/songs/12")
                        .with(SecurityMockMvcRequestPostProcessors.user("customer").roles("CUSTOMER")))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(12))
                .andExpect(jsonPath("$.title").value("Song"));
    }

    @Test
    @DisplayName("Rejects anonymous access to song details")
    void rejects_anonymous_song_details_request() throws Exception {
        mockMvc.perform(get("/api/catalog/songs/12"))
                .andExpect(status().isUnauthorized());

        verify(facade, never()).getSongDetails(12L);
    }

    @Test
    @DisplayName("Creates a song through the MVC endpoint")
    void creates_song_through_mvc() throws Exception {
        // given
        SongDto.Create create = new SongDto.Create(
                "Song", LocalDate.of(2020, 1, 1), 180, SongLanguage.EN,
                null, null, List.of(), null);
        SongDto.Info created = new SongDto.Info(
                12L, "Song", 180, "EN", LocalDate.of(2020, 1, 1), List.of(), null, null);
        when(mapper.toDomainCreate(any())).thenReturn(create);
        when(facade.addSong(create)).thenReturn(created);
        when(mapper.toInfoResponse(created)).thenReturn(new SongApiDto.InfoResponse(
                12L, "Song", 180, "EN", LocalDate.of(2020, 1, 1), List.of(), null, null));

        // when
        mockMvc.perform(post("/api/catalog/songs")
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title":"Song",
                                  "releaseDate":"2020-01-01",
                                  "duration":180,
                                  "language":"EN",
                                  "featArtistIds":[]
                                }
                                """))

                // then
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(12))
                .andExpect(jsonPath("$.title").value("Song"));

        verify(mapper).toDomainCreate(new SongApiDto.CreateRequest(
                "Song", LocalDate.of(2020, 1, 1), 180, SongLanguage.EN,
                null, null, List.of(), null));
        verify(facade).addSong(create);
    }

    @Test
    @DisplayName("Rejects a song with an invalid duration")
    void rejects_song_with_invalid_duration() throws Exception {
        mockMvc.perform(post("/api/catalog/songs")
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title":"Song",
                                  "releaseDate":"2020-01-01",
                                  "duration":0,
                                  "language":"EN"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.duration").value("duration must be a positive number"));

        verify(facade, never()).addSong(any());
    }

    @Test
    @DisplayName("Uploads a full track through the MVC endpoint")
    void uploads_full_track_through_mvc() throws Exception {
        // given
        byte[] audioBytes = {1, 2, 3};
        MockMultipartFile file = new MockMultipartFile("file", "track.wav", "audio/wav", audioBytes);
        SongDto.Track track = new SongDto.Track(12L, "Track uploaded");
        when(facade.addTrackFile(audioBytes, 12L)).thenReturn(track);
        when(mapper.toTrackResponse(track)).thenReturn(new SongApiDto.TrackResponse(12L, "Track uploaded"));

        // when
        mockMvc.perform(multipart("/api/catalog/songs/12/track")
                        .file(file)
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.songId").value(12))
                .andExpect(jsonPath("$.message").value("Track uploaded"));
    }

    @Test
    @DisplayName("Uploads a preview through the MVC endpoint")
    void uploads_preview_through_mvc() throws Exception {
        // given
        byte[] audioBytes = {1, 2, 3};
        MockMultipartFile file = new MockMultipartFile("file", "preview.mp3", "audio/mpeg", audioBytes);
        SongDto.Preview preview = new SongDto.Preview(12L, "https://cdn/preview.mp3");
        when(facade.addPreviewFile(audioBytes, 12L)).thenReturn(preview);
        when(mapper.toPreviewResponse(preview))
                .thenReturn(new SongApiDto.PreviewResponse(12L, "https://cdn/preview.mp3"));

        // when
        mockMvc.perform(multipart("/api/catalog/songs/12/preview")
                        .file(file)
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.songId").value(12))
                .andExpect(jsonPath("$.previewUrl").value("https://cdn/preview.mp3"));
    }

    @Test
    @DisplayName("Rejects an unsupported full track through the MVC endpoint")
    void rejects_unsupported_full_track_through_mvc() throws Exception {
        mockMvc.perform(multipart("/api/catalog/songs/12/track")
                        .file(new MockMultipartFile("file", "track.mp3", "audio/mpeg", new byte[]{1}))
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid upload file"));

        verify(facade, never()).addTrackFile(any(byte[].class), any(Long.class));
    }

    @Test
    @DisplayName("Rejects an empty song preview through the MVC endpoint")
    void rejects_empty_preview_through_mvc() throws Exception {
        mockMvc.perform(multipart("/api/catalog/songs/12/preview")
                        .file(new MockMultipartFile("file", "preview.mp3", "audio/mpeg", new byte[0]))
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Uploaded file must not be empty."));

        verify(facade, never()).addPreviewFile(any(byte[].class), any(Long.class));
    }

    @Test
    @DisplayName("Updates a song through the MVC endpoint")
    void updates_song_through_mvc() throws Exception {
        // given
        SongDto.Update update = new SongDto.Update(
                Optional.of("Updated song"), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
        SongDto.Info updated = new SongDto.Info(
                12L, "Updated song", 180, "EN", LocalDate.of(2020, 1, 1), List.of(), null, null);
        when(mapper.toDomainUpdate(any())).thenReturn(update);
        when(facade.updateSong(12L, update)).thenReturn(updated);
        when(mapper.toInfoResponse(updated)).thenReturn(new SongApiDto.InfoResponse(
                12L, "Updated song", 180, "EN", LocalDate.of(2020, 1, 1), List.of(), null, null));

        // when
        mockMvc.perform(patch("/api/catalog/songs/12")
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Updated song"}
                                """))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(12))
                .andExpect(jsonPath("$.title").value("Updated song"));

        ArgumentCaptor<SongApiDto.UpdateRequest> requestCaptor =
                ArgumentCaptor.forClass(SongApiDto.UpdateRequest.class);
        verify(mapper).toDomainUpdate(requestCaptor.capture());
        assertThat(requestCaptor.getValue().title().get()).isEqualTo("Updated song");
    }

    @Test
    @DisplayName("Deletes a song through the MVC endpoint")
    void deletes_song_through_mvc() throws Exception {
        mockMvc.perform(delete("/api/catalog/songs/12")
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isNoContent());

        verify(facade).deactivateSong(12L);
    }

}
