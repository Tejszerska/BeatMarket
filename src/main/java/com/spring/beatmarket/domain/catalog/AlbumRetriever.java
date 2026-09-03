package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.AlbumDto;
import com.spring.beatmarket.domain.catalog.dto.AlbumInfo;
import com.spring.beatmarket.domain.catalog.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
class AlbumRetriever {
    private final AlbumRepository albumRepository;
    private final AlbumMapper albumMapper;

    AlbumInfo findAlbumByReturnAlbumInfo(final Long id) {
        return albumRepository.findAlbumByIdReturnAlbumInfo(id)
                .orElseThrow(() -> new ResourceNotFoundException("Album", id));
    }

    Slice<AlbumDto.Summary> findAllAlbums(final Long artistId, final String title, final Pageable pageable) {
        boolean hasArtist = artistId != null;
        boolean hasTitle = title != null && !title.isBlank();

        Slice<Album> all;

        if (hasArtist && hasTitle) {
            all = albumRepository.findByActiveTrueAndArtists_IdAndTitleContainingIgnoreCase(artistId, title, pageable);
        } else if (hasArtist) {
            all = albumRepository.findByActiveTrueAndArtists_Id(artistId, pageable);
        } else if (hasTitle) {
            all = albumRepository.findByActiveTrueAndTitleContainingIgnoreCase(title, pageable);
        } else {
            all = albumRepository.findByActiveTrue(pageable);
        }

        return all.map(albumMapper::toSummaryDto);
    }


    Album getActiveWithArtist(final Long id) {
        return albumRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Album", id));
    }

    List<Album> getActiveWithArtist(final List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }

        List<Album> foundAlbums = albumRepository.findActiveWithArtistsByIds(ids);
        if (foundAlbums.size() != new HashSet<>(ids).size()) {

            List<Long> foundIds = foundAlbums.stream().map(Album::getId).toList();
            Long missingId = ids.stream()
                    .filter(id -> !foundIds.contains(id))
                    .findFirst()
                    .orElse(0L);

            throw new ResourceNotFoundException("Album", missingId);
        }
        return foundAlbums;

    }

    Album getActive(final Long id) {
        return albumRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Album", id));
    }
}
