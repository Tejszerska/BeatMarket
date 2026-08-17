package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.LegacyAlbumDto;
import com.spring.beatmarket.domain.catalog.dto.AlbumInfo;
import com.spring.beatmarket.domain.catalog.dto.AlbumRequestDto;
import com.spring.beatmarket.domain.catalog.dto.AlbumSongsDto;
import com.spring.beatmarket.domain.catalog.dto.LegacyArtistDto;
import com.spring.beatmarket.domain.catalog.dto.ArtistRequestDto;
import com.spring.beatmarket.domain.catalog.dto.ArtistWithAlbumDto;
import com.spring.beatmarket.domain.catalog.dto.LegacyGenreDto;
import com.spring.beatmarket.domain.catalog.dto.SaveGenreDto;
import com.spring.beatmarket.domain.catalog.dto.song.CreateSongDto;
import com.spring.beatmarket.domain.catalog.dto.song.SongDetailsDto;
import com.spring.beatmarket.domain.catalog.dto.song.LegacySongDto;
import com.spring.beatmarket.domain.catalog.dto.song.SongSearchCriteria;
import com.spring.beatmarket.domain.catalog.dto.song.SongSummaryDto;
import com.spring.beatmarket.domain.catalog.dto.song.UpdateSongDto;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
@Transactional
public class CatalogFacade {
    private final SongAdder songAdder;
    private final SongRetriever songRetriever;
    private final SongDeleter songDeleter;
    private final SongUpdater songUpdater;
    private final ArtistAdder artistAdder;
    private final GenreAdder genreAdder;
    private final AlbumAdder albumAdder;
    private final ArtistRetriever artistRetriever;
    private final AlbumRetriever albumRetriever;
    private final ArtistDeleter artistDeleter;
    private final ArtistAssigner artistAssigner;
    private final ArtistUpdater artistUpdater;
    private final GenreRetriever genreRetriever;
    private final SongAssigner songAssigner;
    private final GenreDeleter genreDeleter;
    private final GenreUpdater genreUpdater;


    public AlbumInfo findAlbumByIdReturnAlbumInfo(Long id) {
        return albumRetriever.findAlbumByReturnAlbumInfo(id);
    }

    public Slice<LegacyArtistDto> findAllArtists(Pageable pageable) {
        return artistRetriever.findAllArtist(pageable);
    }

    public Slice<SongSummaryDto> findAllSongs(SongSearchCriteria searchCriteria, Pageable pageable) {
        return songRetriever.findAll(searchCriteria, pageable);
    }

    public SongDetailsDto getSongDetailsById(Long id) {
        return songRetriever.getSongDetailsById(id);
    }

    public Slice<LegacyAlbumDto> findAllAlbums(Pageable pageable) {
        return albumRetriever.findAllAlbums(pageable);
    }

    public Slice<LegacyGenreDto> findAllGenres(Pageable pageable) {
        return genreRetriever.findAll(pageable);
    }

    public LegacyArtistDto addArtistWithDefaultAlbumAndSong(ArtistRequestDto dto) {
        return artistAdder.addArtistWithDefaultAlbumAndSong(dto);
    }

    public LegacyGenreDto addGenre(SaveGenreDto dto) {
        return genreAdder.addGenre(dto);
    }

    public LegacyAlbumDto addAlbumWithSong(AlbumRequestDto dto) {
        return albumAdder.addAlbum(dto.songId(), dto.title(), dto.releaseDate());
    }

    public ArtistWithAlbumDto addArtistToAlbum(Long artistId, Long albumId) {
        return artistAssigner.addArtistToAlbum(artistId, albumId);
    }

    public LegacySongDto addSong(final CreateSongDto dto) {
        return songAdder.addSong(dto);
    }

    public LegacyArtistDto addArtist(ArtistRequestDto dto) {
        return artistAdder.addArtist(dto.name());
    }

    public LegacyArtistDto updateArtistNameById(Long artistId, String name) {
        return artistUpdater.updateArtistNameById(artistId, name);
    }


    public void deleteSongById(Long id) {
        songRetriever.existsById(id);
        songDeleter.deleteById(id);
    }

    public void deleteArtistByIdWithAlbumsAndSongs(Long artistId) {
        artistDeleter.deleteArtistByIdWithAlbumsAndSongs(artistId);
    }

    public LegacyGenreDto findGenreById(final Long genreId) {
        return genreRetriever.getGenreDtoById(genreId);
    }

    public LegacySongDto updateSongById(Long id, UpdateSongDto songFromRequest) {
        return songUpdater.updateSongById(id, songFromRequest);
    }

    public void deleteGenreById(final Long genreId) {
        genreRetriever.existsById(genreId);
        genreDeleter.deleteById(genreId);
    }


    public AlbumSongsDto assignSongByIdToAlbumById(final Long albumId, final Long songId) {
        return songAssigner.assignSongByIdToAlbumById(albumId, songId);
    }

    public LegacyGenreDto update(final Long id, final SaveGenreDto dto) {
        return genreUpdater.update(id, dto);
    }
}