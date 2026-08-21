package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.AlbumInfo;
import com.spring.beatmarket.domain.catalog.dto.LegacyAlbumDto;
import com.spring.beatmarket.domain.catalog.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
class AlbumRetriever {
    private final AlbumRepository albumRepository;
    private final ArtistRetriever artistRetriever;
    private final AlbumMapper albumMapper;

    AlbumInfo findAlbumByReturnAlbumInfo(final Long id) {
        return albumRepository.findAlbumByIdReturnAlbumInfo(id)
                .orElseThrow(() -> new ResourceNotFoundException("Album", id));
    }

    Set<Album> findAlbumsByArtistId(final Long artistId) {
        artistRetriever.existsById(artistId);
        return new HashSet<>(albumRepository.findAllAlbumsByArtistId(artistId));
    }

    Set<LegacyAlbumDto> findAlbumsDtoByArtistId(final Long artistId) {
        return findAlbumsByArtistId(artistId)
                .stream().map(albumMapper::mapFromEntityToAlbumDto)
                .collect(Collectors.toSet());
    }

    Album findById(final Long albumId) {
        return albumRepository.findById(albumId)
                .orElseThrow(() -> new  ResourceNotFoundException("Album", albumId));
    }

    Slice<LegacyAlbumDto> findAllAlbums(Pageable pageable) {
        return albumRepository.findAllAlbums(pageable)
                .map(albumMapper::mapFromEntityToAlbumDto);
    }

    void existsById(Long id) {
        if (!albumRepository.existsById(id)) {
            throw new ResourceNotFoundException("Album", id);
        }
    }

    Album getAlbumReferenceById(final Long id) {
        existsById(id);
        return albumRepository.getReferenceById(id);
    }

    Album getActive(final Long id) {
        return albumRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Album", id));
    }
}
