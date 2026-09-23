package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.SongDto;
import com.spring.beatmarket.domain.catalog.exception.MissingRequiredFieldException;
import com.spring.beatmarket.shared.utils.StringUtils;
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
    private final FileStoragePort fileStoragePort;


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
                    songFromDB.getArtists(), songFromDB::clearArtists, songFromDB::assignArtist);
        }

        return songMapper.toInfoDto(songFromDB);
    }

    Integer bulkUpdateSongsByGenreId(final Long oldId, final Long newId) {
        return songRepository.bulkUpdateGenre(oldId, newId, Instant.now());
    }

    void updateTrackFile(final byte[] trackBytes, final Long id) {
        Song song = songRetriever.getLazily(id);

        String initTrackFileKey = song.getTrackFileKey();
        if (initTrackFileKey != null && !initTrackFileKey.isBlank()) {
            fileStoragePort.delete(initTrackFileKey);
        }

        String fileKey = StringUtils.createSlug(song.getTitle()) +
                "-"
                + song.getUuid().toString().substring(0, 4)
                + ".wav";

        fileStoragePort.upload(trackBytes, fileKey);
        log.info("Successfully uploaded full track file for song id={}", song.getId());

        song.changeTrackFileKey(fileKey);
    }
}


