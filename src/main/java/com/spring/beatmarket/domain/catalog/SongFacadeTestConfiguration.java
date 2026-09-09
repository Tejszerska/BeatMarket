package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.licensing.LicensingFacade;

class SongFacadeTestConfiguration {
    static SongFacade createSongFacade(
            SongRepository songRepository,
            GenreRetriever genreRetriever,
            AlbumRetriever albumRetriever,
            LicensingFacade licensingFacade,
            ArtistRoleManager artistRoleManager,
            SongMapper songMapper) {

        SongRetriever songRetriever = new SongRetriever(songRepository, songMapper, licensingFacade);
        SongAdder songAdder = new SongAdder(songRepository, genreRetriever, albumRetriever, artistRoleManager, songMapper);
        SongUpdater songUpdater = new SongUpdater(songRetriever, albumRetriever, genreRetriever, songRepository, songMapper, artistRoleManager);
        SongDeleter songDeleter = new SongDeleter(songRepository, songRetriever, licensingFacade);
        return new SongFacadeImpl(songAdder, songRetriever, songDeleter, songUpdater);
    }
}
