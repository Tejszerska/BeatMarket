package com.spring.beatmarket.domain.catalog;

class AlbumFacadeTestConfiguration {
    static AlbumFacade createAlbumFacade(
            AlbumMapper albumMapper,
            AlbumRepository albumRepository,
            SongRetriever songRetriever,
            ArtistRoleManager artistRoleManager
            ) {

        AlbumRetriever albumRetriever = new AlbumRetriever(albumRepository, albumMapper);
        AlbumAdder albumAdder = new AlbumAdder(songRetriever, albumRepository, albumMapper, artistRoleManager);
        AlbumUpdater albumUpdater = new AlbumUpdater(albumRetriever, artistRoleManager, albumMapper, songRetriever);
        AlbumDeleter albumDeleter = new AlbumDeleter(albumRepository, albumRetriever);
        return new AlbumFacadeImpl(albumRetriever, albumAdder, albumUpdater, albumDeleter);
    }
}
