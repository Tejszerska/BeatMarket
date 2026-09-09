package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.AlbumDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
class AlbumAdder {
    private final SongRetriever songRetriever;
    private final AlbumRepository albumRepository;
    private final AlbumMapper albumMapper;
    private final ArtistRoleManager artistRoleManager;

    AlbumDto.Info add(final AlbumDto.Create createDto) {
        Album album = Album.builder()
                .title(createDto.title())
                .releaseDate(createDto.releaseDate())
                .build();

        artistRoleManager.assign(createDto.mainArtistId(), createDto.featArtistsIds(), "Album", album::assignArtist);

        if (createDto.songIds() != null) {
            Set<Song> songs = songRetriever.getActive(createDto.songIds());

            for (Song song : songs) {
                album.addSong(song);
            }
        }

        Album saved = albumRepository.save(album);
        return albumMapper.toInfoDto(saved);
    }
}
