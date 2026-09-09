package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.AlbumDto;
import com.spring.beatmarket.domain.catalog.exception.MissingRequiredFieldException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


@RequiredArgsConstructor
@Service
class AlbumUpdater {

    private final AlbumRetriever albumRetriever;
    private final ArtistRoleManager artistRoleManager;
    private final AlbumMapper mapper;
    private final SongRetriever songRetriever;

    AlbumDto.Info update(final Long id, final AlbumDto.Update dto) {
        Album album = albumRetriever.getEagerly(id);
        if (dto.title() != null) {
            dto.title().ifPresentOrElse(
                    album::changeTitle,
                    () -> {
                        throw new MissingRequiredFieldException("title");
                    }
            );
        }

        if (dto.releaseDate() != null) {
            dto.releaseDate().ifPresentOrElse(
                    album::changeReleaseDate,
                    () -> {
                        throw new MissingRequiredFieldException("releaseDate");
                    }
            );
        }

        if (dto.mainArtistId() != null || dto.featArtistsIds() != null) {
            artistRoleManager.sync(dto.mainArtistId(), dto.featArtistsIds(),
                    "Album", album.getArtists(), album::removeArtist, album::assignArtist);
        }

        if (dto.songIds() != null) {
            dto.songIds().ifPresentOrElse(
                    newSongIds -> {
                        Set<Song> targetSongs = newSongIds.isEmpty() ?
                                Collections.emptySet() : songRetriever.getActive(newSongIds);

                        Set<Song> currentSongs = new HashSet<>(album.getSongs());

                        for (Song currentSong : currentSongs) {
                            if (!targetSongs.contains(currentSong)) {
                                album.removeSong(currentSong);
                            }
                        }

                        for (Song targetSong : targetSongs) {
                            album.addSong(targetSong);
                        }
                    },
                    album::clearSongs);
        }
        return mapper.toInfoDto(album);
    }
}