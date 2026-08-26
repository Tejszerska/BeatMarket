package com.spring.beatmarket.domain.catalog;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
class ArtistDeleter {
    private final ArtistRetriever artistRetriever;
    private final SongDeleter songDeleter;
    private final AlbumDeleter albumDeleter;

    void deleteById(Long id) {
        log.info("soft deleting artist by id: " + id);
        Artist artist = artistRetriever.findByIdEagerly(id);

        List<Song> songs = new ArrayList<>(artist.getSongs());
        Set<Long> songToDeleteIds = new HashSet<>();

        for (Song song : songs) {
            int order = song.getArtists().indexOf(artist);
            if (order == 0) {
                songToDeleteIds.add(song.getId());
            }
        }
        songDeleter.deleteAllSongsByIds(songToDeleteIds);

        List<Album> albums = artist.getAlbums();
        Set<Long> albumsToDeleteIds = new HashSet<>();
        for (Album album : albums) {
            int order = album.getArtists().indexOf(artist);
            if (order == 0) {
                albumsToDeleteIds.add(album.getId());
            }
        }
        albumDeleter.deleteAllAlbumsByIds(albumsToDeleteIds);

        artist.deactivate();
    }

}
