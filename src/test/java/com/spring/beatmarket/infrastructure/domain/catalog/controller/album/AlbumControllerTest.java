package com.spring.beatmarket.infrastructure.domain.catalog.controller.album;

import com.spring.beatmarket.domain.catalog.AlbumFacade;
import com.spring.beatmarket.domain.catalog.dto.AlbumDto;
import com.spring.beatmarket.infrastructure.upload.InvalidUploadFileException;
import com.spring.beatmarket.infrastructure.upload.UploadFileValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AlbumControllerTest {
    private final AlbumFacade facade = mock(AlbumFacade.class);
    private final AlbumControllerMapper mapper = mock(AlbumControllerMapper.class);
    private final AlbumController controller = new AlbumController(
            facade,
            mapper,
            new UploadFileValidator()
    );

    @Test
    @DisplayName("Searches albums using the supplied filters and page")
    void searches_albums() {
        // given
        Pageable pageable = Pageable.ofSize(10);
        @SuppressWarnings("unchecked")
        Slice<AlbumDto.Summary> slice = mock(Slice.class);
        AlbumApiDto.GetAllResponse expected = new AlbumApiDto.GetAllResponse(List.of(), false);
        when(facade.findAllAlbums(7L, "title", pageable)).thenReturn(slice);
        when(mapper.toGetAllResponse(slice)).thenReturn(expected);

        // when
        var response = controller.searchAlbums("title", 7L, pageable);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expected);
        verify(facade).findAllAlbums(7L, "title", pageable);
    }

    @Test
    @DisplayName("Returns album details for the requested ID")
    void returns_album_details() {
        // given
        AlbumDto.Details details = new AlbumDto.Details(7L, "Album", null, null, List.of(), Set.of());
        AlbumApiDto.DetailsResponse expected = new AlbumApiDto.DetailsResponse(
                7L, "Album", null, null, List.of(), Set.of());
        when(facade.getAlbumDetails(7L)).thenReturn(details);
        when(mapper.toDetailsResponse(details)).thenReturn(expected);

        // when
        var response = controller.getAlbumById(7L);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expected);
    }

    @Test
    @DisplayName("Creates an album and returns a created response")
    void creates_album() {
        // given
        AlbumApiDto.CreateRequest request = new AlbumApiDto.CreateRequest(
                "Album", null, Set.of(), null, List.of());
        AlbumDto.Create domainRequest = new AlbumDto.Create("Album", null, Set.of(), null, List.of());
        AlbumDto.Info created = new AlbumDto.Info(7L, "Album", null, null, List.of(), Set.of());
        AlbumApiDto.InfoResponse expected = new AlbumApiDto.InfoResponse(
                7L, "Album", null, null, List.of(), Set.of());
        when(mapper.toDomainCreate(request)).thenReturn(domainRequest);
        when(facade.addAlbum(domainRequest)).thenReturn(created);
        when(mapper.toInfoResponse(created)).thenReturn(expected);

        // when
        var response = controller.createAlbum(request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(expected);
    }

    @Test
    @DisplayName("Updates an album and returns the updated response")
    void updates_album() {
        // given
        AlbumApiDto.UpdateRequest request = new AlbumApiDto.UpdateRequest(null, null, null, null, null);
        AlbumDto.Update domainRequest = new AlbumDto.Update(
                java.util.Optional.empty(), java.util.Optional.empty(), java.util.Optional.empty(),
                java.util.Optional.empty(), java.util.Optional.empty());
        AlbumDto.Info updated = new AlbumDto.Info(7L, "Album", null, null, List.of(), Set.of());
        AlbumApiDto.InfoResponse expected = new AlbumApiDto.InfoResponse(
                7L, "Album", null, null, List.of(), Set.of());
        when(mapper.toDomainUpdate(request)).thenReturn(domainRequest);
        when(facade.updateAlbum(7L, domainRequest)).thenReturn(updated);
        when(mapper.toInfoResponse(updated)).thenReturn(expected);

        // when
        var response = controller.updateAlbum(7L, request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expected);
    }

    @Test
    @DisplayName("Deactivates an album and returns no content")
    void deactivates_album() {
        // when
        var response = controller.deleteAlbum(7L);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
        verify(facade).deactivateAlbum(7L);
    }

    @Test
    @DisplayName("Uploads a valid album cover and returns its public URL")
    void uploads_album_cover() throws Exception {
        // given
        byte[] imageBytes = {1, 2, 3};
        MockMultipartFile file = new MockMultipartFile("file", "cover.png", "image/png", imageBytes);
        AlbumDto.Cover cover = new AlbumDto.Cover(7L, "Cover uploaded", "https://cdn/cover.webp");
        AlbumApiDto.CoverResponse expected = new AlbumApiDto.CoverResponse(
                7L, "Cover uploaded", "https://cdn/cover.webp");
        when(facade.updateAlbumCover(7L, imageBytes)).thenReturn(cover);
        when(mapper.toCoverResponse(cover)).thenReturn(expected);

        // when
        var response = controller.uploadAlbumCover(7L, file);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expected);
        verify(facade).updateAlbumCover(7L, imageBytes);
    }

    @Test
    @DisplayName("Rejects an unsupported album cover extension before upload")
    void rejects_unsupported_album_cover_extension() {
        // given
        byte[] bytes = {1};
        MockMultipartFile file = new MockMultipartFile("file", "cover.gif", "image/gif", bytes);

        // when & then
        assertThatThrownBy(() -> controller.uploadAlbumCover(7L, file))
                .isInstanceOf(InvalidUploadFileException.class)
                .hasMessage("Invalid file format. Only JPG, JPEG, PNG, and WebP files are allowed.");
        verify(facade, never()).updateAlbumCover(7L, bytes);
        verify(mapper, never()).toCoverResponse(any());
    }

    @Test
    @DisplayName("Rejects an empty album cover before upload")
    void rejects_empty_album_cover() {
        // given
        byte[] bytes = {};
        MockMultipartFile file = new MockMultipartFile("file", "cover.png", "image/png", bytes);

        // when & then
        assertThatThrownBy(() -> controller.uploadAlbumCover(7L, file))
                .isInstanceOf(InvalidUploadFileException.class)
                .hasMessage("Uploaded file must not be empty.");
        verify(facade, never()).updateAlbumCover(7L, bytes);
        verify(mapper, never()).toCoverResponse(any());
    }
}
