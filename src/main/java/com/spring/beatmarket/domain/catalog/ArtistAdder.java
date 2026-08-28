package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.ArtistDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
class ArtistAdder {
    private final ArtistRepository artistRepository;
    private final AlbumRetriever albumRetriever;
    private final SongRetriever songRetriever;

    private final RoleValidator roleValidator;
    private final ArtistMapper artistMapper;

    ArtistDto.Info add(final ArtistDto.Create createDto) {

        Artist newArtist = Artist.builder()
                .name(createDto.name().trim())
                .build();

        Artist savedArtist = artistRepository.save(newArtist);

        List<Long> allSongIds = roleValidator.combineAndValidateIds(createDto.mainSongIds(), createDto.featSongIds(), "Artist", "Song");
        if (!allSongIds.isEmpty()) {
            List<Song> songs = songRetriever.getActiveWithArtist(allSongIds);
            for (Song song : songs) {
                boolean isMain = createDto.mainSongIds() != null && createDto.mainSongIds().contains(song.getId());
                song.assignArtist(savedArtist, isMain);
            }
        }
        List<Long> allAlbumIds = roleValidator.combineAndValidateIds(createDto.mainAlbumIds(), createDto.featAlbumIds(), "Artist", "Album");
        if (!allAlbumIds.isEmpty()) {
            List<Album> albums = albumRetriever.getActiveWithArtist(allAlbumIds);

            for (Album album : albums) {
                boolean isMain = createDto.mainAlbumIds() != null && createDto.mainAlbumIds().contains(album.getId());
                album.assignArtist(savedArtist, isMain);
            }
        }
        return artistMapper.toInfoDto(savedArtist);
    }
}
