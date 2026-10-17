package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.SongDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

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

        if (dto.title() != null) dto.title().ifPresent(songFromDB::changeTitle);
        if (dto.releaseDate() != null) dto.releaseDate().ifPresent(songFromDB::changeReleaseDate);
        if (dto.duration() != null) dto.duration().ifPresent(songFromDB::changeDuration);
        if (dto.language() != null) dto.language().ifPresent(songFromDB::changeLanguage);

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

}
