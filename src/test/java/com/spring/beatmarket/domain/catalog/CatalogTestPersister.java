package com.spring.beatmarket.domain.catalog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;

import java.time.LocalDate;
import java.util.List;

@TestComponent
class CatalogTestPersister {

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private SongRepository songRepository;

    public Artist createAndSaveArtist(String name) {
        return artistRepository.save(Artist.builder().name(name).build());
    }

    public Genre createAndSaveGenre(String name) {
        return genreRepository.save(new Genre(name));
    }

    public Album createAndSaveAlbum(String title) {
        return albumRepository.save(Album.builder().title(title).build());
    }

    public Album createAndSaveAlbumWithArtists(String title, List<Artist> artists) {
        return albumRepository.save(Album.builder().title(title).artists(artists).build());
    }

    public Song createAndSaveSong(String title, Album album) {
        return songRepository.save(Song.builder()
                .title(title)
                .releaseDate(LocalDate.now())
                .duration(200)
                .language(SongLanguage.EN)
                .album(album)
                .build());
    }
}