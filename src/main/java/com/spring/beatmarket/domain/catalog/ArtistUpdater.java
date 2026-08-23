package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.ArtistDto;
import com.spring.beatmarket.domain.catalog.exception.MissingRequiredFieldException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@RequiredArgsConstructor
@Service
class ArtistUpdater {
    private final ArtistRetriever artistRetriever;
    private final ArtistMapper artistMapper;
    private final SongRetriever songRetriever;
    private final AlbumRetriever albumRetriever;

    ArtistDto.Info updateArtist(final Long artistId, final ArtistDto.Update updateFromRequest) {
        Artist artistFromDB = artistRetriever.findByIdEagerly(artistId);

        if (updateFromRequest.name() != null) {
            updateFromRequest.name().ifPresentOrElse(
                    artistFromDB::changeName,
                    () -> {
                        throw new MissingRequiredFieldException("title");
                    }
            );
        }

        if (updateFromRequest.songIds() != null) {
            updateFromRequest.songIds().ifPresentOrElse(
                    newSongsIds -> {
                        List<Song> songsFromDb = songRetriever.getActiveWithArtist(newSongsIds);
                        artistFromDB.changeSongsList(new HashSet<>(songsFromDb));
                    },
                    artistFromDB::clearSongs
            );
        }

        if (updateFromRequest.albumIds() != null) {
            updateFromRequest.albumIds().ifPresentOrElse(
                    newAlbumsIds -> {
                        List<Album> albumsFromDb =  albumRetriever.getActiveWithArtist(newAlbumsIds);
                        artistFromDB.changeAlbumsList(albumsFromDb);
                    },
                    artistFromDB::clearAlbums
            );
        }

        return artistMapper.toInfoDto(artistFromDB);
    }
}
