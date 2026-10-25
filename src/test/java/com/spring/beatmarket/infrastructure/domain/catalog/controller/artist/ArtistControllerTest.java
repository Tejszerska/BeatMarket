package com.spring.beatmarket.infrastructure.domain.catalog.controller.artist;

import com.spring.beatmarket.domain.catalog.ArtistFacade;
import com.spring.beatmarket.domain.catalog.dto.ArtistDto;
import com.spring.beatmarket.domain.catalog.dto.ImageDto;
import com.spring.beatmarket.infrastructure.upload.InvalidUploadFileException;
import com.spring.beatmarket.infrastructure.upload.UploadFileValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ArtistControllerTest {
    private final ArtistFacade facade = mock(ArtistFacade.class);
    private final ArtistControllerMapper mapper = mock(ArtistControllerMapper.class);
    private final ArtistController controller = new ArtistController(facade, mapper, new UploadFileValidator());

    @Test
    @DisplayName("Searches artists by name and page")
    void searches_artists() {
        // given
        Pageable pageable = Pageable.ofSize(10);
        @SuppressWarnings("unchecked")
        Slice<ArtistDto.Summary> slice = mock(Slice.class);
        ArtistApiDto.GetAllResponse expected = new ArtistApiDto.GetAllResponse(List.of(), false);
        when(facade.findAllArtists("artist", pageable)).thenReturn(slice);
        when(mapper.toGetAllResponse(slice)).thenReturn(expected);

        // when
        var response = controller.searchArtists("artist", pageable);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expected);
    }

    @Test
    @DisplayName("Returns artist details for the requested ID")
    void returns_artist_details() {
        // given
        ArtistDto.Details details = new ArtistDto.Details(42L, "Artist", null, List.of(), List.of());
        ArtistApiDto.DetailsResponse expected = new ArtistApiDto.DetailsResponse(
                42L, "Artist", null, List.of(), List.of());
        when(facade.getArtistDetails(42L)).thenReturn(details);
        when(mapper.toDetailsResponse(details)).thenReturn(expected);

        // when
        var response = controller.getArtistById(42L);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expected);
    }

    @Test
    @DisplayName("Creates an artist and returns a created response")
    void creates_artist() {
        // given
        ArtistApiDto.CreateRequest request = new ArtistApiDto.CreateRequest(
                "Artist", List.of(), List.of(), List.of(), List.of());
        ArtistDto.Create domainRequest = new ArtistDto.Create(
                "Artist", List.of(), List.of(), List.of(), List.of());
        ArtistDto.Info created = new ArtistDto.Info(42L, "Artist", null, List.of(), List.of());
        ArtistApiDto.InfoResponse expected = new ArtistApiDto.InfoResponse(
                42L, "Artist", null, List.of(), List.of());
        when(mapper.toDomainCreate(request)).thenReturn(domainRequest);
        when(facade.addArtist(domainRequest)).thenReturn(created);
        when(mapper.toInfoResponse(created)).thenReturn(expected);

        // when
        var response = controller.createArtist(request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(expected);
    }

    @Test
    @DisplayName("Updates an artist and returns the updated response")
    void updates_artist() {
        // given
        ArtistApiDto.UpdateRequest request = new ArtistApiDto.UpdateRequest(null, null, null, null, null);
        ArtistDto.Update domainRequest = new ArtistDto.Update(
                java.util.Optional.empty(), java.util.Optional.empty(), java.util.Optional.empty(),
                java.util.Optional.empty(), java.util.Optional.empty());
        ArtistDto.Info updated = new ArtistDto.Info(42L, "Artist", null, List.of(), List.of());
        ArtistApiDto.InfoResponse expected = new ArtistApiDto.InfoResponse(
                42L, "Artist", null, List.of(), List.of());
        when(mapper.toDomainUpdate(request)).thenReturn(domainRequest);
        when(facade.updateArtist(42L, domainRequest)).thenReturn(updated);
        when(mapper.toInfoResponse(updated)).thenReturn(expected);

        // when
        var response = controller.updateArtist(42L, request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expected);
    }

    @Test
    @DisplayName("Uploads an artist image using the API response contract")
    void uploads_artist_image_using_api_response_contract() throws Exception {
        // given
        byte[] imageBytes = {1, 2, 3};
        MockMultipartFile file = new MockMultipartFile("file", "artist.png", "image/png", imageBytes);
        ImageDto.Upload uploadedImage = new ImageDto.Upload(
                42L,
                "Artist image uploaded successfully",
                "https://storage/artist.webp"
        );
        ArtistApiDto.ImageResponse expectedResponse = new ArtistApiDto.ImageResponse(
                42L,
                "Artist image uploaded successfully",
                "https://storage/artist.webp"
        );
        when(facade.updateArtistImage(42L, imageBytes)).thenReturn(uploadedImage);
        when(mapper.toImageResponse(uploadedImage)).thenReturn(expectedResponse);

        // when
        var response = controller.uploadArtistImage(42L, file);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedResponse);
        verify(facade).updateArtistImage(42L, imageBytes);
    }

    @Test
    @DisplayName("Deactivates an artist and returns no content")
    void deactivates_artist() {
        // when
        var response = controller.deleteArtist(42L);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
        verify(facade).deactivateArtist(42L);
    }

    @Test
    @DisplayName("Rejects an unsupported artist image extension before upload")
    void rejects_unsupported_artist_image_extension() {
        // given
        MockMultipartFile file = new MockMultipartFile("file", "artist.gif", "image/gif", new byte[]{1});

        // when & then
        assertThatThrownBy(() -> controller.uploadArtistImage(42L, file))
                .isInstanceOf(InvalidUploadFileException.class)
                .hasMessage("Invalid file format. Only JPG, JPEG, PNG, and WebP files are allowed.");
        verify(facade, never()).updateArtistImage(eq(42L), any(byte[].class));
    }
}
