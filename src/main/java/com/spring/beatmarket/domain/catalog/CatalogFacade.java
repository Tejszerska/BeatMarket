package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.dto.AlbumInfo;
import com.spring.beatmarket.domain.catalog.dto.AlbumRequestDto;
import com.spring.beatmarket.domain.catalog.dto.AlbumSongsDto;
import com.spring.beatmarket.domain.catalog.dto.ArtistDto;
import com.spring.beatmarket.domain.catalog.dto.ArtistRequestDto;
import com.spring.beatmarket.domain.catalog.dto.GenreDto;
import com.spring.beatmarket.domain.catalog.dto.LegacyAlbumDto;
import com.spring.beatmarket.domain.catalog.dto.SongDto;
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

    public Slice<ArtistDto.Summary> findAllArtists(String name, Pageable pageable) {
        return artistRetriever.findAllArtist(name, pageable);
    }

    public Slice<SongDto.Summary> findAllSongs(SongDto.SearchCriteria searchCriteria, Pageable pageable) {
        return songRetriever.findAll(searchCriteria, pageable);
    }

    public SongDto.Details  getSongDetailsById(Long id) {
        return songRetriever.getSongDetailsById(id);
    }

    public Slice<LegacyAlbumDto> findAllAlbums(Pageable pageable) {
        return albumRetriever.findAllAlbums(pageable);
    }

    public Slice<GenreDto.Summary> findAllGenres(Pageable pageable) {
        return genreRetriever.findAll(pageable);
    }

    public ArtistDto.Info addArtistWithDefaultAlbumAndSong(ArtistRequestDto dto) {
        return artistAdder.addArtistWithDefaultAlbumAndSong(dto);
    }

    public GenreDto.Info addGenre(GenreDto.Create dto) {
        return genreAdder.addGenre(dto);
    }

    public LegacyAlbumDto addAlbumWithSong(AlbumRequestDto dto) {
        return albumAdder.addAlbum(dto.songId(), dto.title(), dto.releaseDate());
    }

//    public ArtistDto.Details addArtistToAlbum(Long artistId, Long albumId) {
//        return artistAssigner.addArtistToAlbum(artistId, albumId);
//    }

    public SongDto.Info addSong(final SongDto.Create dto) {
        return songAdder.addSong(dto);
    }

    public ArtistDto.Info addArtist(ArtistDto.Create createDto) {
        return artistAdder.addArtist(createDto);
    }

    public ArtistDto.Info updateArtist(Long artistId, ArtistDto.Update dto) {
        return artistUpdater.updateArtist(artistId, dto);
    }


    public void deleteSongById(Long id) {
        songDeleter.deleteById(id);
    }

    public void deleteArtistByIdWithAlbumsAndSongs(Long artistId) {
        artistDeleter.deleteArtistByIdWithAlbumsAndSongs(artistId);
    }

    public GenreDto.Details getGenreDetails(final Long genreId) {
        return genreRetriever.getGenreDetails(genreId);
    }

    public SongDto.Info updateSongById(Long id, SongDto.Update songFromRequest) {
        return songUpdater.updateSongById(id, songFromRequest);
    }

    public void deleteGenreById(final Long genreId) {
        genreDeleter.deleteById(genreId);
    }


    public AlbumSongsDto assignSongByIdToAlbumById(final Long albumId, final Long songId) {
        return songAssigner.assignSongByIdToAlbumById(albumId, songId);
    }

    public GenreDto.Info update(final Long id, final GenreDto.Update dto) {
        return genreUpdater.update(id, dto);
    }

    public GenreDto.Transfer transferGenre(final Long oldId, final Long newId) {
        if (oldId.equals(newId)) {
            throw new IllegalArgumentException("Source and target genre IDs cannot be the same.");
        }
        genreRetriever.validateExistsAndActive(oldId);
        genreRetriever.validateExistsAndActive(newId);

        Integer updatedSongsCount = songUpdater.transferGenre(oldId, newId);

        return new GenreDto.Transfer(updatedSongsCount, oldId, newId);
    }
}