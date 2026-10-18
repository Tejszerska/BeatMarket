package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.ImageDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ArtistImageUpdaterTest {
    private final ArtistRetriever artistRetriever = mock(ArtistRetriever.class);
    private final FileKeyGenerator fileKeyGenerator = mock(FileKeyGenerator.class);
    private final ImageUploader imageUploader = mock(ImageUploader.class);
    private final ArtistImageUpdater artistImageUpdater = new ArtistImageUpdater(
            artistRetriever,
            fileKeyGenerator,
            imageUploader
    );

    @Test
    @DisplayName("Uploads an artist image and replaces its file key")
    void uploads_artist_image_and_replaces_file_key() {
        // given
        byte[] imageBytes = {1, 2, 3};
        Artist artist = TestObjectsFactory.createArtistWithId(42L, "Artist Name");
        artist.changeImageFileKey("artists/old-image.webp");
        when(artistRetriever.getActive(42L)).thenReturn(artist);
        when(fileKeyGenerator.generate("artists", "Artist Name", "webp"))
                .thenReturn("artists/artist-name-12345-0123abcd.webp");
        when(imageUploader.upload(imageBytes, "artists/artist-name-12345-0123abcd.webp", "artists/old-image.webp"))
                .thenReturn("https://storage/artist-name.webp");

        // when
        ImageDto.Upload result = artistImageUpdater.update(42L, imageBytes);

        // then
        assertThat(result).isEqualTo(new ImageDto.Upload(
                42L,
                "Artist image uploaded successfully",
                "https://storage/artist-name.webp"
        ));
        assertThat(artist.getImageFileKey()).isEqualTo("artists/artist-name-12345-0123abcd.webp");
        verify(imageUploader).upload(imageBytes, "artists/artist-name-12345-0123abcd.webp", "artists/old-image.webp");
    }
}
