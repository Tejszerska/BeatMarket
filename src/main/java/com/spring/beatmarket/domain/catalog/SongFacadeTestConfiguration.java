package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.licensing.LicensingFacade;

class SongFacadeTestConfiguration {
    static SongFacade createSongFacade(
            SongRepository songRepository,
            GenreRetriever genreRetriever,
            AlbumRetriever albumRetriever,
            ArtistRetriever artistRetriever,
            LicensingFacade licensingFacade,
            RoleValidator roleValidator,
            ArtistRoleAssigner artistRoleAssigner,
            SongMapper songMapper) {

        SongRetriever songRetriever = new SongRetriever(songRepository, songMapper, licensingFacade);
        SongAdder songAdder = new SongAdder(songRepository, genreRetriever, albumRetriever, artistRoleAssigner, songMapper);
        SongUpdater songUpdater = new SongUpdater(songRetriever, albumRetriever, genreRetriever, artistRetriever, songRepository, roleValidator, songMapper);
        SongDeleter songDeleter = new SongDeleter(songRepository, songRetriever, licensingFacade);
        return new SongFacadeImpl(songAdder, songRetriever, songDeleter, songUpdater);
    }
}
