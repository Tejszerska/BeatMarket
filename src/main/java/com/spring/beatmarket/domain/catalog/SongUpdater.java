package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.SongDto;
import com.spring.beatmarket.domain.catalog.exception.MissingRequiredFieldException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

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
    private final SongMapper songMapper;


    SongDto.Info updateSongById(final Long id, final SongDto.Update songFromRequest) {
        Song songFromDB = songRetriever.findSongByIdEagerly(id);

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


        if (songFromRequest.artistIds() != null) {
            songFromRequest.artistIds().ifPresentOrElse(
                    newArtistIds ->
                    {
                        List<Artist> newArtists = artistRetriever.getActiveArtists(newArtistIds);
                        songFromDB.changeArtistList(newArtists);
                    },
                    songFromDB::clearArtists
            );
        }
        return songMapper.toInfoDto(songFromDB);
    }

    Integer transferGenre(final Long oldId, final Long newId) {
       return songRepository.bulkUpdateGenre(oldId, newId);
    }
}


