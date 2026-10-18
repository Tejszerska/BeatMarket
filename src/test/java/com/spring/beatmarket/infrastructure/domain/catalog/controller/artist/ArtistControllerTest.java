package com.spring.beatmarket.infrastructure.domain.catalog.controller.artist;

import com.spring.beatmarket.domain.catalog.ArtistFacade;
import com.spring.beatmarket.domain.catalog.dto.ImageDto;
import com.spring.beatmarket.infrastructure.upload.InvalidUploadFileException;
import com.spring.beatmarket.infrastructure.upload.UploadFileValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;

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
