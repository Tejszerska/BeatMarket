package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.AlbumDto;
import com.spring.beatmarket.domain.catalog.dto.AlbumInfo;
import com.spring.beatmarket.domain.catalog.dto.AlbumRequestDto;
import com.spring.beatmarket.domain.catalog.dto.AlbumSongsDto;
import com.spring.beatmarket.domain.catalog.dto.ArtistDto;
import com.spring.beatmarket.domain.catalog.dto.ArtistRequestDto;
import com.spring.beatmarket.domain.catalog.dto.ArtistWithAlbumDto;
import com.spring.beatmarket.domain.catalog.dto.GenreDto;
import com.spring.beatmarket.domain.catalog.dto.GenreRequestDto;
import com.spring.beatmarket.domain.catalog.dto.SongDto;
import com.spring.beatmarket.domain.catalog.dto.SongRequestDto;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.Set;

@AllArgsConstructor
@Service
@Transactional
public class BeatMarketCrudFacade {
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
    private final GenreAssigner genreAssigner;
    private final SongAssigner songAssigner;
    private final GenreDeleter genreDeleter;

    public Set<AlbumDto> findAlbumsDtoByArtistId(Long artistId) {
        return albumRetriever.findAlbumsDtoByArtistId(artistId);
    }

    public AlbumInfo findAlbumByIdReturnAlbumInfo(Long id) {
        return albumRetriever.findAlbumByReturnAlbumInfo(id);
    }

    public Slice<ArtistDto> findAllArtists(Pageable pageable) {
        return artistRetriever.findAllArtist(pageable);
    }

    public Slice<SongDto> findAllSongs(Pageable pageable) {
        return songRetriever.findAll(pageable);
    }

    public SongDto findSongDtoById(Long id) {
        return songRetriever.findSongDtoById(id);
    }

    public Slice<AlbumDto> findAllAlbums(Pageable pageable) {
        return albumRetriever.findAllAlbums(pageable);
    }

    public Slice<GenreDto> findAllGenres(Pageable pageable) {
        return genreRetriever.findAllGenres(pageable);
    }

    public ArtistDto addArtistWithDefaultAlbumAndSong(ArtistRequestDto dto) {
        return artistAdder.addArtistWithDefaultAlbumAndSong(dto);
    }
    public GenreDto addGenre(GenreRequestDto dto) {
        return genreAdder.addGenre(dto.name());
    }

    public AlbumDto addAlbumWithSong(AlbumRequestDto dto) {
        return albumAdder.addAlbum(dto.songId(), dto.title(), dto.releaseDate());
    }

    public ArtistWithAlbumDto addArtistToAlbum(Long artistId, Long albumId) {
        return artistAssigner.addArtistToAlbum(artistId, albumId);
    }

    public SongDto addSong(final SongRequestDto dto) {
        return songAdder.addSong(dto);
    }

    public ArtistDto addArtist(ArtistRequestDto dto) {
        return artistAdder.addArtist(dto.name());
    }

    public ArtistDto updateArtistNameById(Long artistId, String name) {
        return artistUpdater.updateArtistNameById(artistId, name);
    }



    public void deleteSongById(Long id) {
        songRetriever.existsById(id);
        songDeleter.deleteById(id);
    }

    public void deleteArtistByIdWithAlbumsAndSongs(Long artistId) {
        artistDeleter.deleteArtistByIdWithAlbumsAndSongs(artistId);
    }

    public GenreDto findGenreById(final Long genreId) {
        return genreRetriever.findGenreDtoById(genreId);
    }

    public SongDto updateSongPartiallyById(Long id, SongDto songFromRequest) {
        return songUpdater.updatePartiallyByIdAndDto(id, songFromRequest);
    }

    public int deleteGenreById(final Long genreId) {
      return  genreDeleter.deleteById(genreId);
    }

    public SongDto assignGenreByIdToSongById(final Long songId, final Long genreId) {
        return genreAssigner.assignGenreByIdToSongById(songId, genreId);
    }

    public AlbumSongsDto assignSongByIdToAlbumById(final Long albumId, final Long songId) {
        return songAssigner.assignSongByIdToAlbumById(albumId, songId);
    }

}
