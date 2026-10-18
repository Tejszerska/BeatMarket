package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.ArtistDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ArtistMapperTest {
    private final InMemoryFileStorageAdapter fileStoragePort = new InMemoryFileStorageAdapter();
    private final ArtistMapper artistMapper = new ArtistMapperImpl(new FileStorageUrlMapper(fileStoragePort));

    @Test
    @DisplayName("Maps the artist image URL in each artist payload")
    void maps_artist_image_url_in_each_payload() {
        // given
        Artist artist = TestObjectsFactory.createArtistWithId(42L, "Artist");
        artist.changeImageFileKey("artists/artist.webp");

        // when
        ArtistDto.Summary summary = artistMapper.toSummaryDto(artist);
        ArtistDto.Details details = artistMapper.toDetailsDto(artist);
        ArtistDto.Info info = artistMapper.toInfoDto(artist);
        ArtistDto.Basic basic = artistMapper.toBasicDto(artist, 0);

        // then
        assertThat(summary.imageUrl()).isEqualTo("artists/artist.webp");
        assertThat(details.imageUrl()).isEqualTo("artists/artist.webp");
        assertThat(info.imageUrl()).isEqualTo("artists/artist.webp");
        assertThat(basic.imageUrl()).isEqualTo("artists/artist.webp");
    }
}
