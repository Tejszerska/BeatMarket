package com.spring.beatmarket.infrastructure.domain.catalog.controller.album;

import com.spring.beatmarket.domain.catalog.AlbumFacade;
import com.spring.beatmarket.infrastructure.upload.InvalidUploadFileException;
import com.spring.beatmarket.infrastructure.upload.UploadFileValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class AlbumControllerTest {
    private final AlbumFacade facade = mock(AlbumFacade.class);
    private final AlbumController controller = new AlbumController(
            facade,
            mock(AlbumControllerMapper.class),
            new UploadFileValidator()
    );

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
    }
}
