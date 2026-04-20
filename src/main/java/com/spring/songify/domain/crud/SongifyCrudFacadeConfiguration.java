package com.spring.songify.domain.crud;
class SongifyCrudFacadeConfiguration {

    public static SongifyCrudFacade createSongifyCrud(final SongRepository songRepository,
                                                      final GenreRepository genreRepository,
                                                      final ArtistRepository artistRepository,
                                                      final AlbumRepository albumRepository,
                                                      final SongMapper songMapper,
                                                      final GenreMapper genreMapper,
                                                      final ArtistMapper artistMapper,
                                                      final AlbumMapper albumMapper,
                                                      final ArtistWithAlbumMapper artistWithAlbumMapper
                                                      ){
        GenreRetriever genreRetriever = new GenreRetriever(genreRepository, genreMapper);
        SongRetriever songRetriever = new SongRetriever(songRepository, songMapper);
        SongUpdater songUpdater = new SongUpdater(songRetriever, songMapper);
        AlbumAdder albumAdder = new AlbumAdder(songRetriever, albumRepository, albumMapper);
        ArtistRetriever artistRetriever = new ArtistRetriever(artistRepository, artistMapper);
        AlbumRetriever albumRetriever = new AlbumRetriever(albumRepository, artistRetriever, albumMapper);
        SongDeleter songDeleter = new SongDeleter(songRepository);
        SongAdder songAdder = new SongAdder(songRepository, genreRetriever, songMapper);
        ArtistAdder artistAdder = new ArtistAdder(artistRepository,albumAdder, songAdder, albumRetriever, artistMapper);
        GenreAdder genreAdder = new GenreAdder(genreRepository, genreMapper);
        AlbumDeleter albumDeleter = new AlbumDeleter(albumRepository);
        ArtistDeleter artistDeleter = new ArtistDeleter(artistRetriever, albumRetriever, artistRepository, songDeleter, albumDeleter);
        ArtistAssigner artistAssigner = new ArtistAssigner(artistRetriever, albumRetriever, artistWithAlbumMapper);
        ArtistUpdater artistUpdater = new ArtistUpdater(artistRetriever, artistMapper);
        GenreAssigner genreAssigner = new GenreAssigner(songRetriever, genreRetriever, songMapper);
        SongAssigner songAssigner = new SongAssigner(albumRetriever, songRetriever, songMapper, albumMapper);
        GenreDeleter genreDeleter = new GenreDeleter(genreRepository);
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
                songAssigner,
                genreDeleter
        );
    }
}
