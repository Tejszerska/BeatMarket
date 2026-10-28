package com.spring.beatmarket.infrastructure.domain.catalog.controller.album;

import com.spring.beatmarket.domain.account.UserRepository;
import com.spring.beatmarket.domain.catalog.AlbumFacade;
import com.spring.beatmarket.domain.catalog.dto.AlbumDto;
import com.spring.beatmarket.infrastructure.config.JacksonConfig;
import com.spring.beatmarket.infrastructure.config.WebConfig;
import com.spring.beatmarket.infrastructure.domain.catalog.controller.artist.ArtistApiDto;
import com.spring.beatmarket.infrastructure.security.SecurityMvcTestConfiguration;
import com.spring.beatmarket.infrastructure.upload.UploadFileValidator;

import java.util.List;
import java.util.Set;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AlbumController.class)
@Import({WebConfig.class, JacksonConfig.class, SecurityMvcTestConfiguration.class, UploadFileValidator.class})
class AlbumControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AlbumFacade facade;

    @MockitoBean
    private AlbumControllerMapper mapper;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private AuthenticationSuccessHandler successHandler;

    @Test
    @DisplayName("Returns albums through the public MVC endpoint")
    void returns_albums_through_mvc() throws Exception {
        // given
        @SuppressWarnings("unchecked")
        Slice<AlbumDto.Summary> slice = mock(Slice.class);
        AlbumApiDto.GetAllResponse response = new AlbumApiDto.GetAllResponse(
                List.of(new AlbumApiDto.SummaryResponse(7L, "Album", null,
                        List.of(new ArtistApiDto.Reference(3L, "Artist")))), false);
        when(facade.findAllAlbums(eq(3L), eq("Album"), any(Pageable.class))).thenReturn(slice);
        when(mapper.toGetAllResponse(slice)).thenReturn(response);

        // when
        mockMvc.perform(get("/api/catalog/albums").param("title", "Album").param("artistId", "3"))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.albums[0].id").value(7))
                .andExpect(jsonPath("$.albums[0].title").value("Album"))
                .andExpect(jsonPath("$.albums[0].artists[0].id").value(3))
                .andExpect(jsonPath("$.albums[0].artists[0].name").value("Artist"))
                .andExpect(jsonPath("$.hasNext").value(false));

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(facade).findAllAlbums(eq(3L), eq("Album"), pageableCaptor.capture());
        assertThat(pageableCaptor.getValue().getPageNumber()).isZero();
        assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(20);
        assertThat(pageableCaptor.getValue().getSort().getOrderFor("editedOn").getDirection())
                .isEqualTo(Sort.Direction.ASC);
    }

    @Test
    @DisplayName("Returns album details through the MVC endpoint")
    void returns_album_details_through_mvc() throws Exception {
        // given
        AlbumDto.Details details = new AlbumDto.Details(7L, "Album", null, null, List.of(), Set.of());
        when(facade.getAlbumDetails(7L)).thenReturn(details);
        when(mapper.toDetailsResponse(details))
                .thenReturn(new AlbumApiDto.DetailsResponse(7L, "Album", null, null, List.of(), Set.of()));

        // when
        mockMvc.perform(get("/api/catalog/albums/7"))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.title").value("Album"));
    }

    @Test
    @DisplayName("Creates an album through the MVC endpoint")
    void creates_album_through_mvc() throws Exception {
        // given
        AlbumDto.Create create = new AlbumDto.Create("Album", null, Set.of(), null, List.of());
        AlbumDto.Info created = new AlbumDto.Info(7L, "Album", null, null, List.of(), Set.of());
        AlbumApiDto.CreateRequest request = new AlbumApiDto.CreateRequest(
                "Album", null, Set.of(), null, List.of());
        when(mapper.toDomainCreate(request)).thenReturn(create);
        when(facade.addAlbum(create)).thenReturn(created);
        when(mapper.toInfoResponse(created))
                .thenReturn(new AlbumApiDto.InfoResponse(7L, "Album", null, null, List.of(), Set.of()));

        // when
        mockMvc.perform(post("/api/catalog/albums")
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Album","songIds":[],"featArtistsIds":[]}
                                """))

                // then
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.title").value("Album"));

        verify(mapper).toDomainCreate(request);
        verify(facade).addAlbum(create);
    }

    @Test
    @DisplayName("Rejects an album request with a blank title")
    void rejects_album_with_blank_title() throws Exception {
        mockMvc.perform(post("/api/catalog/albums")
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":" "}
                                """))
                .andExpect(status().isBadRequest());

        verify(facade, never()).addAlbum(any());
    }

    @Test
    @DisplayName("Updates an album through the MVC endpoint")
    void updates_album_through_mvc() throws Exception {
        // given
        AlbumDto.Update update = new AlbumDto.Update(
                java.util.Optional.of("Updated album"), java.util.Optional.empty(), java.util.Optional.empty(),
                java.util.Optional.empty(), java.util.Optional.empty());
        AlbumDto.Info updated = new AlbumDto.Info(7L, "Updated album", null, null, List.of(), Set.of());
        when(mapper.toDomainUpdate(any())).thenReturn(update);
        when(facade.updateAlbum(7L, update)).thenReturn(updated);
        when(mapper.toInfoResponse(updated))
                .thenReturn(new AlbumApiDto.InfoResponse(7L, "Updated album", null, null, List.of(), Set.of()));

        // when
        mockMvc.perform(patch("/api/catalog/albums/7")
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Updated album"}
                                """))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.title").value("Updated album"));

        ArgumentCaptor<AlbumApiDto.UpdateRequest> requestCaptor =
                ArgumentCaptor.forClass(AlbumApiDto.UpdateRequest.class);
        verify(mapper).toDomainUpdate(requestCaptor.capture());
        assertThat(requestCaptor.getValue().title().get()).isEqualTo("Updated album");
        verify(facade).updateAlbum(7L, update);
    }

    @Test
    @DisplayName("Uploads an album cover through the MVC endpoint")
    void uploads_album_cover_through_mvc() throws Exception {
        // given
        byte[] imageBytes = {1, 2, 3};
        MockMultipartFile file = new MockMultipartFile("file", "cover.png", "image/png", imageBytes);
        AlbumDto.Cover cover = new AlbumDto.Cover(7L, "Cover uploaded", "https://cdn/cover.webp");
        when(facade.updateAlbumCover(7L, imageBytes)).thenReturn(cover);
        when(mapper.toCoverResponse(cover))
                .thenReturn(new AlbumApiDto.CoverResponse(7L, "Cover uploaded", "https://cdn/cover.webp"));

        // when
        mockMvc.perform(multipart("/api/catalog/albums/7/cover")
                        .file(file)
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.albumId").value(7))
                .andExpect(jsonPath("$.coverUrl").value("https://cdn/cover.webp"));
    }

    @Test
    @DisplayName("Rejects an unsupported album cover through the MVC endpoint")
    void rejects_unsupported_album_cover_through_mvc() throws Exception {
        mockMvc.perform(multipart("/api/catalog/albums/7/cover")
                        .file(new MockMultipartFile("file", "cover.gif", "image/gif", new byte[]{1}))
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid upload file"));

        verify(facade, never()).updateAlbumCover(any(Long.class), any(byte[].class));
    }

    @Test
    @DisplayName("Rejects an empty album cover through the MVC endpoint")
    void rejects_empty_album_cover_through_mvc() throws Exception {
        mockMvc.perform(multipart("/api/catalog/albums/7/cover")
                        .file(new MockMultipartFile("file", "cover.png", "image/png", new byte[0]))
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Uploaded file must not be empty."));

        verify(facade, never()).updateAlbumCover(any(Long.class), any(byte[].class));
    }

    @Test
    @DisplayName("Deletes an album through the MVC endpoint")
    void deletes_album_through_mvc() throws Exception {
        mockMvc.perform(delete("/api/catalog/albums/7")
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isNoContent());

        verify(facade).deactivateAlbum(7L);
    }
}
