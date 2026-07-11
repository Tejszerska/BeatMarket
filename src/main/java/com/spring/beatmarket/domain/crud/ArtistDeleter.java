package com.spring.beatmarket.domain.crud;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
class ArtistDeleter {
    private final ArtistRetriever artistRetriever;
    private final AlbumRetriever albumRetriever;
    private final ArtistRepository artistRepository;
    private final SongDeleter songDeleter;
    private final AlbumDeleter albumDeleter;

    void deleteArtistByIdWithAlbumsAndSongs(final Long artistId) {
        Artist artist = artistRetriever.findById(artistId);
        Set<Album> artistsAlbums = albumRetriever.findAlbumsByArtistId(artistId);
        if (artistsAlbums.isEmpty()) {
            log.info("Artist with id={} doesn't have any albums", artistId);
            artistRepository.deleteArtistById(artistId);
            return;
        }
        // get this artist solo albums
        Set<Album> albumsWithOneArtist = artistsAlbums.stream()
                .filter(album -> album.getArtists().size() == 1)
                .collect(Collectors.toSet());

        // get albums with 2 or more artists
        Stream<Album> albumsWithMultipleArtists = artistsAlbums.stream()
                .filter(album -> album.getArtists().size() >= 2);

        // remove artist from albums with 2+ artists
        albumsWithMultipleArtists.forEach(album -> album.removeArtist(artist));


        // flatmap song ids from solo albums
        Set<Long> allSongsIdsFromAllAlbumsWithOnlyThisArtist = albumsWithOneArtist.stream()
                .flatMap(album -> album.getSongs().stream())
                .map(Song::getId)
                .collect(Collectors.toSet());
        // delete songs
        songDeleter.deleteAllSongsById(allSongsIdsFromAllAlbumsWithOnlyThisArtist);
        // get solo album ids
        Set<Long> albumIdsWithThisArtist = albumsWithOneArtist.stream()
                .map(Album::getId)
                .collect(Collectors.toSet());
        // delete solo albums
        albumDeleter.deleteAllAlbumsByIds(albumIdsWithThisArtist);
        // delete this artist
        artistRepository.deleteArtistById(artistId);
    }
}
