package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.ArtistDto;
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

    ArtistDto.Info update(final Long artistId, final ArtistDto.Update dto) {
        Artist artist = artistRetriever.findEagerly(artistId);
        if (dto.name() != null) dto.name().ifPresent(artist::changeName);

        if (dto.mainSongIds() != null || dto.featSongIds() != null) {
            Set<Song> allCurrentSongs = artist.getSongs();

            List<Long> currentMainSongIds = allCurrentSongs.stream()
                    .filter(song -> song.getArtists().indexOf(artist) == 0)
                    .map(Song::getId)
                    .toList();

            List<Long> currentFeatSongIds = allCurrentSongs.stream()
                    .filter(song -> song.getArtists().indexOf(artist) > 0)
                    .map(Song::getId)
                    .toList();

            List<Long> targetMainSongIds = dto.mainSongIds() == null ?
                    currentMainSongIds : dto.mainSongIds().orElse(Collections.emptyList());

            List<Long> targetFeatSongIds = dto.featSongIds() == null ?
                    currentFeatSongIds : dto.featSongIds().orElse(Collections.emptyList());


            Set<Long> allTargetSongIds = roleValidator.combineAndValidateIds(
                    targetMainSongIds, targetFeatSongIds, "Artist", "Song"
            );

            List<Song> newSongs = songRetriever.getActiveWithArtist(allTargetSongIds);
            List<Song> currentSongsCopy = new ArrayList<>(allCurrentSongs);

            for (Song song : currentSongsCopy) {
                if (!allTargetSongIds.contains(song.getId())) {
                    List<Artist> currentSongArtists = song.getArtists();
                    roleValidator.validateIsMainArtist(currentSongArtists, artist, song.getId(), "Song");
                    song.removeArtist(artist);
                }
            }

            for (Song song : newSongs) {
                boolean isMain = targetMainSongIds.contains(song.getId());
                song.assignArtist(artist, isMain);
            }
        }

        if (dto.mainAlbumIds() != null || dto.featAlbumIds() != null) {

            Set<Album> allCurrentAlbums = artist.getAlbums();

            List<Long> currentMainAlbumsIds = allCurrentAlbums.stream()
                    .filter(album -> album.getArtists().indexOf(artist) == 0)
                    .map(Album::getId)
                    .toList();

            List<Long> currentFeatAlbumsIds = allCurrentAlbums.stream()
                    .filter(album -> album.getArtists().indexOf(artist) > 0)
                    .map(Album::getId)
                    .toList();

            List<Long> targetMainAlbumsIds = dto.mainAlbumIds() == null ?
                    currentMainAlbumsIds : dto.mainAlbumIds().orElse(Collections.emptyList());

            List<Long> targetFeatAlbumsIds = dto.featAlbumIds() == null ?
                    currentFeatAlbumsIds : dto.featAlbumIds().orElse(Collections.emptyList());

            Set<Long> allTargetIds = roleValidator.combineAndValidateIds(targetMainAlbumsIds, targetFeatAlbumsIds, "Artist", "Album");

            List<Album> newAlbums = albumRetriever.getActiveWithArtist(allTargetIds);
            List<Album> oldAlbumsCopy = new ArrayList<>(allCurrentAlbums);

            for (Album oldAlbum : oldAlbumsCopy) {
                if (!allTargetIds.contains(oldAlbum.getId())) {
                    List<Artist> artists = oldAlbum.getArtists();
                    roleValidator.validateIsMainArtist(artists, artist, oldAlbum.getId(), "Album");

                    oldAlbum.removeArtist(artist);
                }
            }
            for (Album newAlbum : newAlbums) {
                boolean isMain = targetMainAlbumsIds.contains(newAlbum.getId());
                newAlbum.assignArtist(artist, isMain);
            }
        }
        return artistMapper.toInfoDto(artist);
    }
}
