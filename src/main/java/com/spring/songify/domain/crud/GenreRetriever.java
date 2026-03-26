package com.spring.songify.domain.crud;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
class GenreRetriever {
    private final GenreRepository genreRepository;
    private final SongRepository songRepository;

}
