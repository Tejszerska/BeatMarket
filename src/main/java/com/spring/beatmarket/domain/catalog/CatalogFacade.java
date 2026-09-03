package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.AlbumInfo;
import com.spring.beatmarket.domain.catalog.dto.AlbumRequestDto;
import com.spring.beatmarket.domain.catalog.dto.LegacyAlbumDto;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
@Transactional
public class CatalogFacade {
    private final AlbumAdder albumAdder;
    private final AlbumRetriever albumRetriever;

    public AlbumInfo findAlbumByIdReturnAlbumInfo(Long id) {
        return albumRetriever.findAlbumByReturnAlbumInfo(id);
    }



    public LegacyAlbumDto addAlbumWithSong(AlbumRequestDto dto) {
        return albumAdder.addAlbum(dto.songId(), dto.title(), dto.releaseDate());
    }
}