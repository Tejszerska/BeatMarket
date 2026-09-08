package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.AlbumDto;
import com.spring.beatmarket.domain.catalog.dto.LegacyAlbumDto;
import com.spring.beatmarket.domain.catalog.exception.TitleIsBlankException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
class AlbumAdder {
    private final SongRetriever songRetriever;
    private final AlbumRepository albumRepository;
    private final AlbumMapper albumMapper;
    private final ArtistRoleAssigner artistRoleAssigner;

    LegacyAlbumDto addAlbum(final Long songId, final String title, final LocalDate releaseDate) {
        if (title == null || title.isBlank()) throw new TitleIsBlankException("Album needs a specified title!");
        Song songById = songRetriever.getEagerly(songId);
        Album album = new Album();
        album.changeTitle(title);
        album.addSong(songById);
        album.changeReleaseDate(releaseDate);
        return albumMapper.mapFromEntityToAlbumDto(albumRepository.save(album));
    }

    AlbumDto.Info add(final AlbumDto.Create createDto) {
        Set<Song> songs = createDto.songIds() != null
                ? songRetriever.getActive(createDto.songIds()) : null;

        Album album = Album.builder()
                .title(createDto.title())
                .releaseDate(createDto.releaseDate())
                .songs(songs)
                .build();

        artistRoleAssigner.assign(createDto.mainArtistId(), createDto.featuredArtistsIds(), "Album", album::assignArtist);

        return albumMapper.toInfoDto(albumRepository.save(album));
    }


}
