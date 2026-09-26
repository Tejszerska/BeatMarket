package com.spring.beatmarket.domain.catalog;

class ArtistFacadeTestConfiguration {
    static ArtistFacade createArtistFacade(
            ArtistRepository artistRepository,
            AlbumRetriever albumRetriever,
            SongRetriever songRetriever,
            RoleValidator roleValidator,
            ArtistMapper artistMapper,
            SongDeleter songDeleter,
            AlbumDeleter albumDeleter
            ) {

        ArtistAdder artistAdder = new ArtistAdder(artistRepository, albumRetriever, songRetriever, roleValidator, artistMapper);
        ArtistRetriever artistRetriever = new ArtistRetriever(artistRepository, artistMapper);
        ArtistDeleter artistDeleter = new ArtistDeleter(artistRetriever, songDeleter, albumDeleter);
        ArtistUpdater artistUpdater = new ArtistUpdater( artistRetriever, artistMapper, songRetriever, albumRetriever, roleValidator);

        return new ArtistFacadeImpl(artistAdder, artistRetriever, artistDeleter, artistUpdater);
    }
}