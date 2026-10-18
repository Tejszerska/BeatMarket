package com.spring.beatmarket.domain.catalog;

class AlbumFacadeTestConfiguration {
    static AlbumFacade createAlbumFacade(
            AlbumMapper albumMapper,
            AlbumRepository albumRepository,
            SongRetriever songRetriever,
            ArtistRoleManager artistRoleManager,
            ImageProcessorPort imageProcessor,
            FileStoragePort fileStoragePort
            ) {

        AlbumRetriever albumRetriever = new AlbumRetriever(albumRepository, albumMapper);
        AlbumAdder albumAdder = new AlbumAdder(songRetriever, albumRepository, albumMapper, artistRoleManager);
        AlbumUpdater albumUpdater = new AlbumUpdater(albumRetriever, artistRoleManager, albumMapper, songRetriever);
        ImageUploader imageUploader = new ImageUploader(imageProcessor, fileStoragePort);
        AlbumCoverUpdater albumCoverUpdater = new AlbumCoverUpdater(
                albumRetriever,
                new FileKeyGenerator(),
                imageUploader
        );
        AlbumDeleter albumDeleter = new AlbumDeleter(albumRepository, albumRetriever);
        return new AlbumFacadeImpl(albumRetriever, albumAdder, albumUpdater, albumCoverUpdater, albumDeleter);
    }
}
