package com.spring.beatmarket.domain.catalog;

class AlbumFacadeTestConfiguration {
    static AlbumFacade createAlbumFacade(
            AlbumMapper albumMapper,
            AlbumRepository albumRepository,
            SongRetriever songRetriever,
            ArtistRoleManager artistRoleManager,
            AlbumCoverImageProcessorPort imageProcessor,
            FileStoragePort fileStoragePort
            ) {

        AlbumRetriever albumRetriever = new AlbumRetriever(albumRepository, albumMapper);
        AlbumAdder albumAdder = new AlbumAdder(songRetriever, albumRepository, albumMapper, artistRoleManager);
        AlbumUpdater albumUpdater = new AlbumUpdater(albumRetriever, artistRoleManager, albumMapper, songRetriever);
        AlbumCoverUpdater albumCoverUpdater = new AlbumCoverUpdater(
                albumRetriever,
                imageProcessor,
                fileStoragePort
        );
        AlbumDeleter albumDeleter = new AlbumDeleter(albumRepository, albumRetriever);
        return new AlbumFacadeImpl(albumRetriever, albumAdder, albumUpdater, albumCoverUpdater, albumDeleter);
    }
}
