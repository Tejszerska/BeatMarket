package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.SongDto;
import com.spring.beatmarket.domain.catalog.exception.MissingRequiredFieldException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Log4j2
@RequiredArgsConstructor
@Service
class SongUpdater {
    private final SongRetriever songRetriever;
    private final AlbumRetriever albumRetriever;
    private final GenreRetriever genreRetriever;
    private final ArtistRetriever artistRetriever;

    private final SongRepository songRepository;

    private final RoleValidator roleValidator;
    private final SongMapper songMapper;


    SongDto.Info update(final Long id, final SongDto.Update songFromRequest) {
        Song songFromDB = songRetriever.getEagerly(id);

        if (songFromRequest.title() != null) {
            songFromRequest.title().ifPresentOrElse(
                    songFromDB::changeTitle,
                    () -> {
                        throw new MissingRequiredFieldException("title");
                    }
            );
        }

        if (songFromRequest.releaseDate() != null) {
            songFromRequest.releaseDate().ifPresentOrElse(
                    songFromDB::changeReleaseDate,
                    () -> {
                        throw new MissingRequiredFieldException("releaseDate");
                    }
            );
        }

        if (songFromRequest.duration() != null) {
            songFromRequest.duration().ifPresentOrElse(
                    songFromDB::changeDuration,
                    () -> {
                        throw new MissingRequiredFieldException("duration");
                    }
            );
        }

        if (songFromRequest.language() != null) {
            songFromRequest.language().ifPresentOrElse(
                    songFromDB::changeLanguage,
                    () -> {
                        throw new MissingRequiredFieldException("language");
                    }
            );
        }


        if (songFromRequest.genreId() != null) {
            songFromRequest.genreId().ifPresentOrElse(
                    newGenreId ->
                    {
                        Genre genreProxy = genreRetriever.getActive(newGenreId);
                        songFromDB.assignToGenre(genreProxy);

                    },
                    songFromDB::detachFromGenre
            );
        }

        if (songFromRequest.albumId() != null) {
            songFromRequest.albumId().ifPresentOrElse(
                    newAlbumId ->
                    {
                        Album albumProxy = albumRetriever.getActive(newAlbumId);
                        songFromDB.assignToAlbum(albumProxy);

                    },
                    songFromDB::detachFromAlbum
            );
        }


        if (songFromRequest.mainArtistId() != null || songFromRequest.featArtistIds() != null) {
            List<Artist> allCurrentArtists = new ArrayList<>(songFromDB.getArtists());

            Long currentMainId = allCurrentArtists.isEmpty() ? null : allCurrentArtists.get(0).getId();
            List<Long> currentFeatIds = allCurrentArtists.stream().skip(1).map(Artist::getId).toList();

            Long targetMainId = songFromRequest.mainArtistId() == null
                    ? currentMainId
                    : songFromRequest.mainArtistId().orElse(null);

            List<Long> targetFeatIds = songFromRequest.featArtistIds() == null
                    ? currentFeatIds
                    : songFromRequest.featArtistIds().orElse(Collections.emptyList());

            List<Long> targetMainList = targetMainId != null ? List.of(targetMainId) : null;

            List<Long> allTargetIds = roleValidator.combineAndValidateIds(
                    targetMainList, targetFeatIds, "Song", "Artist"
            );

            for (Artist oldArtist : allCurrentArtists) {
                if (!allTargetIds.contains(oldArtist.getId())) {
                    songFromDB.removeArtist(oldArtist);
                }
            }

            if (!allTargetIds.isEmpty()) {
                List<Artist> newArtists = artistRetriever.getActive(allTargetIds);
                for (Artist artist : newArtists) {
                    boolean isMain = targetMainId != null && targetMainId.equals(artist.getId());
                    songFromDB.assignArtist(artist, isMain);
                }
            }
        }

        return songMapper.toInfoDto(songFromDB);
    }

    Integer bulkUpdateSongsByGenreId(final Long oldId, final Long newId) {
       return songRepository.bulkUpdateGenre(oldId, newId, Instant.now());
    }
}


