package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.AlbumDto;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
@Transactional
class AlbumFacadeImpl implements AlbumFacade {
    private final AlbumRetriever albumRetriever;
    private final AlbumAdder albumAdder;
    private final AlbumUpdater albumUpdater;
    private final AlbumDeleter albumDeleter;

    public Slice<AlbumDto.Summary> findAllAlbums(final Long artistId, final String title, final Pageable pageable) {
        return albumRetriever.findAllAlbums(artistId, title, pageable);
    }

    public AlbumDto.Details getAlbumDetails(final Long albumId) {

        return albumRetriever.getDetails(albumId);
    }

    public AlbumDto.Info addAlbum(final AlbumDto.Create createDto) {
        return albumAdder.add(createDto);
    }

    public AlbumDto.Info updateAlbum(final Long id, final AlbumDto.Update dto) {
        return albumUpdater.update(id, dto);
    }

    public void deactivateAlbum(final Long id) {
        albumDeleter.deactivate(id);
    }
}
