package com.spring.beatmarket.domain.catalog;

class GenreFacadeTestConfiguration {
    static GenreFacade createGenreFacade(
            GenreRepository genreRepository,
            GenreMapper genreMap,
            SongRetriever songRetriever,
            SongUpdater songUpdater) {
        GenreAdder genreAdder = new GenreAdder(genreRepository, genreMap);
        GenreRetriever genreRetriever = new GenreRetriever(genreRepository, genreMap);
        GenreDeleter genreDeleter = new GenreDeleter(genreRetriever, songRetriever);
        GenreUpdater genreUpdater = new GenreUpdater(genreRepository, genreRetriever, genreMap);

        return new GenreFacadeImpl(genreAdder, genreRetriever, genreDeleter, genreUpdater, songUpdater);
    }
}
