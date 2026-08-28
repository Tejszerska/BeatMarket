package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.ArtistDto;
import com.spring.beatmarket.domain.catalog.exception.MissingRequiredFieldException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;


@RequiredArgsConstructor
@Service
class ArtistUpdater {
    private final ArtistRetriever artistRetriever;
    private final ArtistMapper artistMapper;
    private final SongRetriever songRetriever;
    private final AlbumRetriever albumRetriever;
    private final RoleValidator roleValidator;

    ArtistDto.Info update(final Long artistId, final ArtistDto.Update updateFromRequest) {
        Artist artistFromDB = artistRetriever.findEagerly(artistId);

        if (updateFromRequest.name() != null) {
            updateFromRequest.name().ifPresentOrElse(
                    artistFromDB::changeName,
                    () -> {
                        throw new MissingRequiredFieldException("title");
                    }
            );
        }

        if (updateFromRequest.mainSongIds() != null || updateFromRequest.featSongIds() != null) {
            Set<Song> allCurrentSongs = artistFromDB.getSongs();

            List<Long> currentMainSongIds = allCurrentSongs.stream()
                    .filter(song -> song.getArtists().indexOf(artistFromDB) == 0)
                    .map(Song::getId)
                    .toList();

            List<Long> currentFeatSongIds = allCurrentSongs.stream()
                    .filter(song -> song.getArtists().indexOf(artistFromDB) > 0)
                    .map(Song::getId)
                    .toList();

            List<Long> targetMainSongIds = updateFromRequest.mainSongIds() == null ?
                    currentMainSongIds : updateFromRequest.mainSongIds().orElse(Collections.emptyList());

            List<Long> targetFeatSongIds = updateFromRequest.featSongIds() == null ?
                    currentFeatSongIds : updateFromRequest.featSongIds().orElse(Collections.emptyList());


            List<Long> allTargetSongIds = roleValidator.combineAndValidateIds(
                    targetMainSongIds, targetFeatSongIds, "Artist", "Song"
            );

            List<Song> newSongs = songRetriever.getActiveWithArtist(allTargetSongIds);
            List<Song> currentSongsCopy = new ArrayList<>(allCurrentSongs);

            for (Song song : currentSongsCopy) {
                if (!allTargetSongIds.contains(song.getId())) {
                    song.removeArtist(artistFromDB);
                }
            }

            for (Song song : newSongs) {
                boolean isMain = targetMainSongIds.contains(song.getId());
                song.assignArtist(artistFromDB, isMain);
            }
        }

//        spr czy wysłano w request
        if (updateFromRequest.mainAlbumIds() != null || updateFromRequest.featAlbumIds() != null) {

            List<Album> allCurrentAlbums = artistFromDB.getAlbums();

            List<Long> currentMainAlbumsIds = allCurrentAlbums.stream()
                    .filter(album -> album.getArtists().indexOf(artistFromDB) == 0)
                    .map(Album::getId)
                    .toList();

            List<Long> currentFeatAlbumsIds = allCurrentAlbums.stream()
                    .filter(album -> album.getArtists().indexOf(artistFromDB) > 0)
                    .map(Album::getId)
                    .toList();

            List<Long> targetMainAlbumsIds = updateFromRequest.mainAlbumIds() == null ?
                    currentMainAlbumsIds : updateFromRequest.mainAlbumIds().orElse(Collections.emptyList());

            List<Long> targetFeatAlbumsIds = updateFromRequest.featAlbumIds() == null ?
                    currentFeatAlbumsIds : updateFromRequest.featAlbumIds().orElse(Collections.emptyList());

            List<Long> allTargetIds = roleValidator.combineAndValidateIds(targetMainAlbumsIds, targetFeatAlbumsIds, "Artist", "Album");

            List<Album> newAlbums = albumRetriever.getActiveWithArtist(allTargetIds);
            List<Album> oldAlbumsCopy = new ArrayList<>(allCurrentAlbums);

            for (Album oldAlbum : oldAlbumsCopy) {
                if (!allTargetIds.contains(oldAlbum.getId())) {
                    oldAlbum.removeArtist(artistFromDB);
                }
            }
            for (Album newAlbum : newAlbums) {
                boolean isMain = targetMainAlbumsIds.contains(newAlbum.getId());
                newAlbum.assignArtist(artistFromDB, isMain);
            }
        }
        return artistMapper.toInfoDto(artistFromDB);
    }
}
