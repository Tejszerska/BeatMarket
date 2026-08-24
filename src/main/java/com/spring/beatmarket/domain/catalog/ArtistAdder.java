package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.ArtistDto;
import com.spring.beatmarket.domain.catalog.dto.ArtistRequestDto;
import com.spring.beatmarket.domain.catalog.dto.LegacyAlbumDto;
import com.spring.beatmarket.domain.catalog.exception.DataConflictException;
import com.spring.beatmarket.domain.catalog.exception.NameIsBlankException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Service
class ArtistAdder {
    private final ArtistRepository artistRepository;
    private final AlbumAdder albumAdder;
    private final AlbumRetriever albumRetriever;
    private final ArtistMapper artistMapper;
    private final SongRetriever songRetriever;


    ArtistDto.Info addArtist(final ArtistDto.Create createDto) {

        Artist newArtist = Artist.builder()
                .name(createDto.name().trim())
                .build();

        Artist savedArtist = artistRepository.save(newArtist);

        List<Long> allSongIds = combineAndValidateIds(createDto.mainSongIds(), createDto.featSongIds(), "Song");
        if (!allSongIds.isEmpty()) {
            List<Song> songs = songRetriever.getActiveWithArtist(allSongIds);

            for (Song song : songs) {
                boolean isMain = createDto.mainSongIds() != null && createDto.mainSongIds().contains(song.getId());

                if (isMain) {
                    if (!song.getArtists().isEmpty()) {
                        throw new DataConflictException(String.format("Song by id='%s' already has a main artist", song.getId()));
                    }
                    song.getArtists().add(0, savedArtist);
                } else {
                    if (song.getArtists().isEmpty()) {
                        throw new DataConflictException(String.format("Cannot add featured artist to Song by id='%s' without a main artist", song.getId()));
                    }
                    song.getArtists().add(savedArtist);
                }
                savedArtist.getSongs().add(song);
            }
        }


        List<Long> allAlbumIds = combineAndValidateIds(createDto.mainAlbumIds(), createDto.featAlbumIds(), "Album");
        if (!allAlbumIds.isEmpty()) {
            List<Album> albums = albumRetriever.getActiveWithArtist(allAlbumIds);

            for (Album album : albums) {
                boolean isMain = createDto.mainAlbumIds() != null && createDto.mainAlbumIds().contains(album.getId());

                if (isMain) {
                    if (!album.getArtists().isEmpty()) {
                        throw new DataConflictException(String.format("Album by id='%s' already has a main artist", album.getId()));
                    }
                    album.getArtists().add(0, savedArtist);
                } else {
                    if (album.getArtists().isEmpty()) {
                        throw new DataConflictException(String.format("Cannot add featured artist to Album by id='%s' without a main artist", album.getId()));
                    }
                    album.getArtists().add(savedArtist);
                }
                savedArtist.getAlbums().add(album);
            }
        }

        return artistMapper.toInfoDto(savedArtist);
    }

    private List<Long> combineAndValidateIds(List<Long> mainIds, List<Long> featIds, String entityName) {
        List<Long> safeMain = mainIds != null ? mainIds : Collections.emptyList();
        List<Long> safeFeat = featIds != null ? featIds : Collections.emptyList();

        Set<Long> conflictingIds = safeMain.stream()
                .filter(safeFeat::contains)
                .collect(Collectors.toSet());

        if (!conflictingIds.isEmpty()) {
            throw new DataConflictException(
                    String.format("Artist cannot be both main and featured on the same %s. Conflicting IDs: %s",
                            entityName, conflictingIds)
            );
        }

        return Stream.concat(safeMain.stream(), safeFeat.stream()).toList();
    }


    ArtistDto.Info addArtistWithDefaultAlbumAndSong(final ArtistRequestDto dto) {
        String artistName = dto.name();
        if (artistName == null || artistName.isBlank())
            throw new NameIsBlankException("Artist needs a specified name!");
        return artistMapper.toInfoDto(saveArtistWithDefaultAlbumAndSong(artistName));
    }

    private Artist saveArtistWithDefaultAlbumAndSong(final String name) {
        Artist artist = new Artist(name);
        Artist savedArtist = artistRepository.save(artist);

        //LegacySongDto songDtoOld = new LegacySongDto();
        LegacyAlbumDto legacyAlbumDto = albumAdder.addDefaultAlbum(1L);
        Album album = albumRetriever.findById(legacyAlbumDto.id());

        artist.addAlbum(album);
        album.addArtist(artist);
        return savedArtist;
    }
}
