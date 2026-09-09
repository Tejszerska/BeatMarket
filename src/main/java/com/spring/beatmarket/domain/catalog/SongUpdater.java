package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.SongDto;
import com.spring.beatmarket.domain.catalog.exception.MissingRequiredFieldException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Log4j2
@RequiredArgsConstructor
@Service
class SongUpdater {
    private final SongRetriever songRetriever;
    private final AlbumRetriever albumRetriever;
    private final GenreRetriever genreRetriever;
    private final SongRepository songRepository;

    private final SongMapper songMapper;
    private final ArtistRoleManager artistRoleManager;


    SongDto.Info update(final Long id, final SongDto.Update dto) {
        Song songFromDB = songRetriever.getEagerly(id);

        if (dto.title() != null) {
            dto.title().ifPresentOrElse(
                    songFromDB::changeTitle,
                    () -> {
                        throw new MissingRequiredFieldException("title");
                    }
            );
        }

        if (dto.releaseDate() != null) {
            dto.releaseDate().ifPresentOrElse(
                    songFromDB::changeReleaseDate,
                    () -> {
                        throw new MissingRequiredFieldException("releaseDate");
                    }
            );
        }

        if (dto.duration() != null) {
            dto.duration().ifPresentOrElse(
                    songFromDB::changeDuration,
                    () -> {
                        throw new MissingRequiredFieldException("duration");
                    }
            );
        }

        if (dto.language() != null) {
            dto.language().ifPresentOrElse(
                    songFromDB::changeLanguage,
                    () -> {
                        throw new MissingRequiredFieldException("language");
                    }
            );
        }


        if (dto.genreId() != null) {
            dto.genreId().ifPresentOrElse(
                    newGenreId ->
                    {
                        Genre genreProxy = genreRetriever.getActive(newGenreId);
                        songFromDB.assignToGenre(genreProxy);

                    },
                    songFromDB::detachFromGenre
            );
        }

        if (dto.albumId() != null) {
            dto.albumId().ifPresentOrElse(
                    newAlbumId ->
                    {
                        Album albumProxy = albumRetriever.getActive(newAlbumId);
                        songFromDB.assignToAlbum(albumProxy);

                    },
                    songFromDB::detachFromAlbum
            );
        }


        if (dto.mainArtistId() != null || dto.featArtistIds() != null) {
            artistRoleManager.sync(dto.mainArtistId(), dto.featArtistIds(), "Song",
                    songFromDB.getArtists(), songFromDB::removeArtist ,songFromDB::assignArtist);
        }

        return songMapper.toInfoDto(songFromDB);
    }

    Integer bulkUpdateSongsByGenreId(final Long oldId, final Long newId) {
        return songRepository.bulkUpdateGenre(oldId, newId, Instant.now());
    }
}


