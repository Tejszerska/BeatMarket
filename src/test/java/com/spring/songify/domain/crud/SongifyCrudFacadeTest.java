package com.spring.songify.domain.crud;

import com.spring.songify.domain.crud.dto.AlbumDto;
import com.spring.songify.domain.crud.dto.AlbumInfo;
import com.spring.songify.domain.crud.dto.AlbumRequestDto;
import com.spring.songify.domain.crud.dto.ArtistDto;
import com.spring.songify.domain.crud.dto.ArtistRequestDto;
import com.spring.songify.domain.crud.dto.SongDto;
import com.spring.songify.domain.crud.dto.SongLanguageDto;
import com.spring.songify.domain.crud.dto.SongRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SongifyCrudFacadeTest {

    SongifyCrudFacade songifyCrudFacade = SongifyCrudFacadeConfiguration.createSongifyCrud(
            new InMemorySongRepository(),
            new InMemoryGenreRepository(),
            new InMemoryArtistRepository(),
            new InMemoryAlbumRepository()
    );


    @Test
    @DisplayName("Should add artist 'bono' with id=0 when 'bono' was sent")
    void should_add_artist_bono_with_id_zero_when_bono_was_sent() {
        // given
        List<ArtistDto> allArtists = songifyCrudFacade.findAllArtists(Pageable.unpaged()).getContent();
        assertTrue(allArtists.isEmpty());
        ArtistRequestDto bono = ArtistRequestDto.builder()
                .name("bono")
                .build();

        // when
        ArtistDto result = songifyCrudFacade.addArtist(bono);
        // then
        assertThat(result.id()).isEqualTo(0L);
        assertThat(result.name()).isEqualTo("bono");
        assertThat(songifyCrudFacade.findAllArtists(Pageable.unpaged()).getContent().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should throw ArtistNotFoundException when id=0")
    void should_throw_exception_artist_not_found_when_id_was_zero() {
        // given
        assertThat(songifyCrudFacade.findAllArtists(Pageable.unpaged())).isEmpty();
        // when
        Throwable throwable = catchThrowable(() -> songifyCrudFacade.deleteArtistByIdWithAlbumsAndSongs(0L));
        // then
        assertThat(throwable).isInstanceOf(ArtistNotFoundException.class);
        assertThat(throwable.getMessage()).isEqualTo("Artist by id=0 not found");
    }

    @Test
    @DisplayName("Should delete Artist when it has no Album")
    void should_delete_artist_when_no_albums() {
        // given
        ArtistRequestDto request = ArtistRequestDto.builder()
                .name("bono")
                .build();
        Long artistId = songifyCrudFacade.addArtist(request).id();
        assertThat(songifyCrudFacade.findAlbumsDtoByArtistId(artistId)).isEmpty();
        // when
        songifyCrudFacade.deleteArtistByIdWithAlbumsAndSongs(artistId);
        // then
        assertThat(songifyCrudFacade.findAllArtists(Pageable.unpaged())).isEmpty();
    }

    @Test
    @DisplayName("Should delete Artist by Id when Artist has 1 Album and was the only one on that Album")
    void should_delete_artist_by_id_when_has_one_album_and_is_only_artist_on_given_album() {
        // given
        ArtistRequestDto request = ArtistRequestDto.builder()
                .name("bono")
                .build();
        Long artistId = songifyCrudFacade.addArtist(request).id();

        SongRequestDto song = SongRequestDto.builder()
                .name("song1")
                .languageDto(SongLanguageDto.ENGLISH)
                .build();
        SongDto songDto = songifyCrudFacade.addSong(song);
        Long songId = songDto.id();

        AlbumRequestDto album1 = AlbumRequestDto.builder()
                .title("album1")
                .songId(songId)
                .releaseDate(Instant.now())
                .build();
        AlbumDto albumDto = songifyCrudFacade.addAlbumWithSong(album1);
        Long albumId = albumDto.id();
        songifyCrudFacade.addArtistToAlbum(artistId, albumId);
        assertThat(songifyCrudFacade.findAlbumsDtoByArtistId(artistId).size()).isEqualTo(1);
        // when
        songifyCrudFacade.deleteArtistByIdWithAlbumsAndSongs(artistId);
        // then
        assertThat(songifyCrudFacade.findAllArtists(Pageable.unpaged())).isEmpty();
        assertThat(songifyCrudFacade.findAlbumsDtoByArtistId(artistId)).isEmpty();
        assertThatThrownBy(() -> songifyCrudFacade.findSongDtoById(songId))
                .isInstanceOf(SongNotFoundException.class)
                .hasMessage("Song with id 0 not found");
    }

    @Test
    @DisplayName("Should add album with song")
    void should_add_album_with_song() {
        // given
        SongRequestDto songRequestDto = SongRequestDto.builder()
                .name("song1")
                .languageDto(SongLanguageDto.ENGLISH)
                .build();
        SongDto songDto = songifyCrudFacade.addSong(songRequestDto);
        Long songId = songDto.id();

        AlbumRequestDto album1 = AlbumRequestDto.builder()
                .title("album1")
                .songId(songId)
                .releaseDate(Instant.now())
                .build();
        assertThat(songifyCrudFacade.findAllAlbums(Pageable.unpaged())).isEmpty();

        // when
        AlbumDto albumDto = songifyCrudFacade.addAlbumWithSong(album1);
        // then
        assertThat(songifyCrudFacade.findAllAlbums(Pageable.unpaged()).getContent().size()).isEqualTo(1);
        AlbumInfo albumWithSongs = songifyCrudFacade.findAlbumByIdReturnAlbumInfo(albumDto.id());
        Set<AlbumInfo.SongInfo> songs = albumWithSongs.getSongs();
        assertTrue(songs.stream().anyMatch(song -> song.getId().equals(songDto.id())));
    }

    @Test
    @DisplayName("Should remove Artist from Album when Album has 2+ Artists")
    void should_only_remove_artist_from_album_when_more_than_two_artists() {
        // given
        ArtistRequestDto request1 = ArtistRequestDto.builder()
                .name("artist-1")
                .build();
        Long artistId1 = songifyCrudFacade.addArtist(request1).id();
        ArtistRequestDto request2 = ArtistRequestDto.builder()
                .name("artist-2")
                .build();
        Long artistId2 = songifyCrudFacade.addArtist(request2).id();
        assertThat(songifyCrudFacade.findAllArtists(Pageable.unpaged()).getContent().size()).isEqualTo(2);

        SongRequestDto song1 = SongRequestDto.builder()
                .name("song-1")
                .languageDto(SongLanguageDto.ENGLISH)
                .build();
        SongDto songDto1 = songifyCrudFacade.addSong(song1);
        Long songId1 = songDto1.id();

        AlbumRequestDto album1 = AlbumRequestDto.builder()
                .title("album-1")
                .songId(songId1)
                .releaseDate(Instant.now())
                .build();
        AlbumDto albumDto1 = songifyCrudFacade.addAlbumWithSong(album1);
        Long albumId = albumDto1.id();

        songifyCrudFacade.addArtistToAlbum(artistId1, albumId);
        songifyCrudFacade.addArtistToAlbum(artistId2, albumId);
        Set<AlbumDto> albumsByArtist1 = songifyCrudFacade.findAlbumsDtoByArtistId(artistId1);
        Set<AlbumDto> albumsByArtist2 = songifyCrudFacade.findAlbumsDtoByArtistId(artistId2);
        assertThat(albumsByArtist1).isEqualTo(albumsByArtist2);

        SongRequestDto song2 = SongRequestDto.builder()
                .name("song-2")
                .languageDto(SongLanguageDto.ENGLISH)
                .build();
        SongDto songDto2 = songifyCrudFacade.addSong(song2);
        Long songId2 = songDto2.id();

        AlbumRequestDto album2 = AlbumRequestDto.builder()
                .title("album-2")
                .songId(songId2)
                .releaseDate(Instant.now())
                .build();
        AlbumDto albumDto2 = songifyCrudFacade.addAlbumWithSong(album2);
        Long albumId2 = albumDto2.id();
        songifyCrudFacade.addArtistToAlbum(artistId1, albumId2);

        assertThat(songifyCrudFacade.findAlbumsDtoByArtistId(artistId1).size()).isEqualTo(2);
        // when
        songifyCrudFacade.deleteArtistByIdWithAlbumsAndSongs(artistId1);
        //then
        assertThat(songifyCrudFacade.findAlbumsDtoByArtistId(artistId1)).isEmpty();
        assertThat(songifyCrudFacade.findAlbumsDtoByArtistId(artistId2))
                .hasSize(1)
                .contains(albumDto1);
    }

    @Test
    void should_throw_exception_when_song_not_found() {
        // given
        assertThat(songifyCrudFacade.findAllSongs(Pageable.unpaged())).isEmpty();
        // when & then
        assertThatThrownBy(() -> songifyCrudFacade.findSongDtoById(10L))
                .isInstanceOf(SongNotFoundException.class)
                .hasMessage("Song with id 10 not found");
    }

    @Test
    void should_throw_exception_when_album_not_found() {
        // given
        assertThat(songifyCrudFacade.findAllAlbums(Pageable.unpaged())).isEmpty();
        // when & then
        assertThatThrownBy(() -> songifyCrudFacade.findAlbumByIdReturnAlbumInfo(10L))
                .isInstanceOf(AlbumNotFoundException.class)
                .hasMessage("Album with id=10 not found");
    }

    @Test
    @DisplayName("Should return an Album by Album's ID")
    void should_return_album_by_id() {
        // given
        SongRequestDto songRequestDto = SongRequestDto.builder()
                .name("test-song")
                .languageDto(SongLanguageDto.ENGLISH)
                .build();
        SongDto songDto = songifyCrudFacade.addSong(songRequestDto);
        Long songId = songDto.id();

        AlbumRequestDto album1 = AlbumRequestDto.builder()
                .title("test-album")
                .songId(songId)
                .releaseDate(Instant.now())
                .build();
        assertThat(songifyCrudFacade.findAllAlbums(Pageable.unpaged())).isEmpty();
        Long albumId = songifyCrudFacade.addAlbumWithSong(album1).id();
        assertThat(songifyCrudFacade.findAllAlbums(Pageable.unpaged()).getContent().size()).isEqualTo(1);
        // when
        AlbumInfo albumInfo = songifyCrudFacade.findAlbumByIdReturnAlbumInfo(albumId);
        // then
        assertThat(albumInfo.getId()).isEqualTo(albumId);
        assertThat(albumInfo.getTitle()).isEqualTo(album1.title());
        Set<AlbumInfo.SongInfo> songs = albumInfo.getSongs();
        assertThat(songs.size()).isEqualTo(1);
        assertTrue(songs.stream().anyMatch(song -> song.getId().equals(songDto.id())));
    }

    @Test
    @DisplayName("Should add Artist to Album")
    void should_add_artist_to_album() {
        // given
        ArtistRequestDto request = ArtistRequestDto.builder()
                .name("test-artist")
                .build();
        Long artistId = songifyCrudFacade.addArtist(request).id();

        SongRequestDto songRequestDto = SongRequestDto.builder()
                .name("test-song")
                .languageDto(SongLanguageDto.ENGLISH)
                .build();
        SongDto songDto = songifyCrudFacade.addSong(songRequestDto);
        Long songId = songDto.id();

        AlbumRequestDto album1 = AlbumRequestDto.builder()
                .title("test-album")
                .songId(songId)
                .releaseDate(Instant.now())
                .build();

        assertThat(songifyCrudFacade.findAllAlbums(Pageable.unpaged())).isEmpty();
        Long albumId = songifyCrudFacade.addAlbumWithSong(album1).id();
        assertThat(songifyCrudFacade.findAllAlbums(Pageable.unpaged()).getContent().size()).isEqualTo(1);
        assertThat(songifyCrudFacade.findAlbumsDtoByArtistId(artistId)).isEmpty();
        // when
        songifyCrudFacade.addArtistToAlbum(artistId, albumId);
        // then
        Set<AlbumDto> albumsDtoByArtistId = songifyCrudFacade.findAlbumsDtoByArtistId(artistId);
        assertThat(albumsDtoByArtistId.size()).isEqualTo(1);
        assertTrue(albumsDtoByArtistId.stream().allMatch(dto -> dto.id().equals(albumId)));

    }

    @Test
    @DisplayName("Should add Song")
    void should_add_song() {
        // given
        Instant instantNow = Instant.now();
        SongRequestDto requestDto = SongRequestDto.builder()
                .name("test-song")
                .releaseDate(instantNow)
                .languageDto(SongLanguageDto.OTHER)
                .duration(100L)
                .build();
        assertThat(songifyCrudFacade.findAllSongs(Pageable.unpaged())).isEmpty();
        // when
        SongDto songDto = songifyCrudFacade.addSong(requestDto);
        // then
        assertThat(songifyCrudFacade.findAllSongs(Pageable.unpaged()).getContent().size()).isEqualTo(1);
        assertThat(songDto.id()).isNotNull();
        assertThat(songDto.name()).isEqualTo(requestDto.name());
    }


}