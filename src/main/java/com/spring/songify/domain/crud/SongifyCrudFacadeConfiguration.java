package com.spring.songify.domain.crud;

class SongifyCrudFacadeConfiguration {

    public static SongifyCrudFacade createSongifyCrud(final SongRepository songRepository,
                                                      final GenreRepository genreRepository,
                                                      final ArtistRepository artistRepository,
                                                      final AlbumRepository albumRepository){
        GenreRetriever genreRetriever = new GenreRetriever(genreRepository);
        SongRetriever songRetriever = new SongRetriever(songRepository);
        SongUpdater songUpdater = new SongUpdater(songRepository, songRetriever, genreRetriever);
        AlbumAdder albumAdder = new AlbumAdder(songRetriever, albumRepository);
        ArtistRetriever artistRetriever = new ArtistRetriever(artistRepository);
        AlbumRetriever albumRetriever = new AlbumRetriever(albumRepository, artistRetriever);
        SongDeleter songDeleter = new SongDeleter(songRepository);
        SongAdder songAdder = new SongAdder(songRepository, genreRetriever);
        ArtistAdder artistAdder = new ArtistAdder(artistRepository,albumAdder, songAdder, albumRetriever, songRetriever);
        GenreAdder genreAdder = new GenreAdder(genreRepository);
        AlbumDeleter albumDeleter = new AlbumDeleter(albumRepository);
        ArtistDeleter artistDeleter = new ArtistDeleter(artistRetriever, albumRetriever, artistRepository, songDeleter, albumDeleter);
        ArtistAssigner artistAssigner = new ArtistAssigner(artistRetriever, albumRetriever);
        ArtistUpdater artistUpdater = new ArtistUpdater(artistRetriever);
        GenreAssigner genreAssigner = new GenreAssigner(songRetriever, genreRetriever);
        SongAssigner songAssigner = new SongAssigner(albumRetriever, songRetriever);
        return new SongifyCrudFacade(
                songAdder,
                songRetriever,
                songDeleter,
                songUpdater,
                artistAdder,
                genreAdder,
                albumAdder,
                artistRetriever,
                albumRetriever,
                artistDeleter,
                artistAssigner,
                artistUpdater,
                genreRetriever,
                genreAssigner,
                songAssigner
        );
    }
}
