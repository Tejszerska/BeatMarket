//package com.spring.beatmarket.domain.catalog;
//
//import com.spring.beatmarket.domain.catalog.dto.LegacyAlbumDto;
//import com.spring.beatmarket.domain.catalog.dto.AlbumInfo;
//import com.spring.beatmarket.domain.catalog.dto.AlbumRequestDto;
//import com.spring.beatmarket.domain.catalog.dto.LegacyArtistDto;
//import com.spring.beatmarket.domain.catalog.dto.ArtistRequestDto;
//import com.spring.beatmarket.domain.catalog.dto.LegacyGenreDto;
//import com.spring.beatmarket.domain.catalog.dto.SaveGenreDto;
//import com.spring.beatmarket.domain.catalog.dto.SongCreatedDto;
//import com.spring.beatmarket.domain.catalog.dto.LegacySongDto;
//import com.spring.beatmarket.domain.catalog.dto.song.CreateSongDto;
//import com.spring.beatmarket.domain.catalog.exception.AlbumNotFoundException;
//import com.spring.beatmarket.domain.catalog.exception.ArtistNotFoundException;
//import com.spring.beatmarket.domain.catalog.exception.GenreNotfoundException;
//import com.spring.beatmarket.domain.catalog.exception.NameIsBlankException;
//import com.spring.beatmarket.domain.catalog.exception.SongNotFoundException;
//import com.spring.beatmarket.domain.catalog.exception.TitleIsBlankException;
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.NullAndEmptySource;
//import org.junit.jupiter.params.provider.ValueSource;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Slice;
//
//import java.time.LocalDate;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//import static org.assertj.core.api.Assertions.catchThrowable;
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//
//@Disabled("Temporarily disabled - untill catalog module refactor is completed ")
//class BeatMarketCrudFacadeTest {
//
//    BeatMarketCrudFacade beatmarketCrudFacade = BeatMarketCrudFacadeConfiguration.createBeatMarketCrud(
//            new InMemorySongRepository(),
//            new InMemoryGenreRepository(),
//            new InMemoryArtistRepository(),
//            new InMemoryAlbumRepository(),
//            new SongMapperImpl(new GenreMapperImpl()),
//            new GenreMapperImpl(),
//            new ArtistMapperImpl(),
//            new AlbumMapperImpl(),
//            new ArtistWithAlbumMapperImpl(new ArtistMapperImpl(), new AlbumMapperImpl())
//    );
//    //    public Set<LegacyAlbumDto> findAlbumsDtoByArtistId(Long artistId)
//    @Test
//    @DisplayName("Should return one Album by Artists's ID")
//    void should_return_one_album_by_artist_id() {
//        // given
//        LegacyAlbumDto albumDto = createAlbumWithNewSong("test-album", "test-song");
//        LegacyArtistDto artistDto = createArtist("test-artist");
//
//        assertThat(beatmarketCrudFacade.findAlbumsDtoByArtistId(artistDto.id())).isEmpty();
//
//        beatmarketCrudFacade.addArtistToAlbum(artistDto.id(), albumDto.id());
//
//        // when
//        Set<LegacyAlbumDto> albumsDtoByArtistId = beatmarketCrudFacade.findAlbumsDtoByArtistId(artistDto.id());
//
//        // then
//        assertThat(albumsDtoByArtistId)
//                .hasSize(1)
//                .extracting(LegacyAlbumDto::id)
//                .containsExactly(albumDto.id());
//    }
//
//    @Test
//    @DisplayName("Should return two Albums by Artists's ID")
//    void should_return_two_albums_by_artist_id() {
//        // given
//        LegacyAlbumDto albumDto1 = createAlbumWithNewSong("test-album1", "test-song1");
//        LegacyAlbumDto albumDto2 = createAlbumWithNewSong("test-album2", "test-song2");
//        LegacyArtistDto artistDto = createArtist("test-artist");
//
//        assertThat(beatmarketCrudFacade.findAlbumsDtoByArtistId(artistDto.id())).isEmpty();
//
//        beatmarketCrudFacade.addArtistToAlbum(artistDto.id(), albumDto1.id());
//        beatmarketCrudFacade.addArtistToAlbum(artistDto.id(), albumDto2.id());
//
//        // when
//        Set<LegacyAlbumDto> albumsDtoByArtistId = beatmarketCrudFacade.findAlbumsDtoByArtistId(artistDto.id());
//
//        // then
//        assertThat(albumsDtoByArtistId)
//                .hasSize(2)
//                .extracting(LegacyAlbumDto::id)
//                .containsExactlyInAnyOrder(albumDto1.id(), albumDto2.id());
//    }
//
//    @Test
//    @DisplayName("Should return empty Set<LegacyAlbumDto> when no Albums assigned to artist")
//    void should_return_empty_set_of_albums_when_no_albums_assigned_to_artist() {
//        // given
//        LegacyArtistDto artistDto = createArtist("test-artist");
//
//        // when
//        Set<LegacyAlbumDto> albumsDtoByArtistId = beatmarketCrudFacade.findAlbumsDtoByArtistId(artistDto.id());
//
//        // then
//        assertThat(albumsDtoByArtistId).isEmpty();
//    }
//
//    @Test
//    @DisplayName("Should throw ArtistNotFoundException when artist does not exist")
//    void should_throw_exception_when_artist_not_found() {
//        // given
//        Long nonExistingArtistId = 999L;
//
//        // when & then
//        assertThatThrownBy(() -> beatmarketCrudFacade.findAlbumsDtoByArtistId(nonExistingArtistId))
//                .isInstanceOf(ArtistNotFoundException.class)
//                .hasMessageContaining("Artist by id=");
//    }
//
//    //    public AlbumInfo findAlbumByIdReturnAlbumInfo(Long id)
//
//    @Test
//    @DisplayName("Should return an Album by Album's ID")
//    void should_return_album_by_id() {
//        // given
//        LegacySongDto songDto = createSong("test-song");
//        assertThat(beatmarketCrudFacade.findAllAlbums(Pageable.unpaged())).isEmpty();
//
//        LegacyAlbumDto albumDto = createAlbum("test-album", songDto.id());
//        assertThat(beatmarketCrudFacade.findAllAlbums(Pageable.unpaged()).getContent()).hasSize(1);
//
//        // when
//        AlbumInfo albumInfo = beatmarketCrudFacade.findAlbumByIdReturnAlbumInfo(albumDto.id());
//
//        // then
//        assertThat(albumInfo.getId()).isEqualTo(albumDto.id());
//        assertThat(albumInfo.getTitle()).isEqualTo("test-album");
//        assertThat(albumInfo.getSongs())
//                .hasSize(1)
//                .extracting(AlbumInfo.SongInfo::getId)
//                .containsExactly(songDto.id());
//    }
//
//    @Test
//    void should_throw_exception_when_album_not_found() {
//        // given
//        assertThat(beatmarketCrudFacade.findAllAlbums(Pageable.unpaged())).isEmpty();
//
//        // when & then
//        assertThatThrownBy(() -> beatmarketCrudFacade.findAlbumByIdReturnAlbumInfo(10L))
//                .isInstanceOf(AlbumNotFoundException.class)
//                .hasMessage("Album with id=10 not found");
//    }
//
//    //    public Slice<LegacyArtistDto> findAllArtists(Pageable pageable)
//
//    @Test
//    @DisplayName("Should return correct Slice<LegacyArtistDto> when pagination is applied")
//    public void should_return_correct_slice_artistdto_with_pagination() {
//        // given
//        LegacyArtistDto test1 = createArtist("test-1");
//        LegacyArtistDto test2 = createArtist("test-2");
//        LegacyArtistDto test3 = createArtist("test-3");
//
//        // when
//        Pageable pageRequest = PageRequest.of(0, 2);
//        Slice<LegacyArtistDto> firstPage = beatmarketCrudFacade.findAllArtists(pageRequest);
//
//        // then
//        assertThat(firstPage)
//                .containsExactlyInAnyOrder(test1, test2);
//        assertTrue(firstPage.hasNext());
//
//        // when
//        pageRequest = PageRequest.of(1, 2);
//        Slice<LegacyArtistDto> secondPage = beatmarketCrudFacade.findAllArtists(pageRequest);
//
//        // then
//        assertThat(secondPage)
//                .containsExactly(test3);
//        assertFalse(secondPage.hasNext());
//    }
//
//    @Test
//    @DisplayName("Should return Slice<LegacyArtistDto> with one object")
//    public void should_return_one_artist() {
//        //given
//        LegacyArtistDto test = createArtist("test");
//        //when
//        Slice<LegacyArtistDto> allArtists = beatmarketCrudFacade.findAllArtists(Pageable.unpaged());
//        //then
//        assertThat(allArtists)
//                .hasSize(1)
//                .contains(test);
//    }
//
//    @Test
//    @DisplayName("Should return Slice<LegacyArtistDto> with two objects")
//    public void should_return_two_artists() {
//        //given
//        LegacyArtistDto test1 = createArtist("test-1");
//        LegacyArtistDto test2 = createArtist("test-2");
//        //when
//        Slice<LegacyArtistDto> allArtists = beatmarketCrudFacade.findAllArtists(Pageable.unpaged());
//        //then
//        assertThat(allArtists)
//                .containsExactlyInAnyOrder(test1, test2);
//    }
//
//    @Test
//    @DisplayName("Should return Slice<LegacyArtistDto> with zero objects")
//    public void should_return_zero_artists() {
//        //when
//        Slice<LegacyArtistDto> allArtists = beatmarketCrudFacade.findAllArtists(Pageable.unpaged());
//        //then
//        assertThat(allArtists)
//                .isEmpty();
//    }
//
//////    @TODO Fix after implementing filtering for all songs
////    //    public Slice<LegacySongDto> findAllSongs(Pageable pageable)
////
////    @Test
////    @DisplayName("Should return correct Slice<SongSummeryDto> when pagination is applied")
////    public void should_return_correct_slice_SongSummeryDto_with_pagination() {
////        // given
////        LegacySongDto test1 = createSong("test-1");
////        LegacySongDto test2 = createSong("test-2");
////        LegacySongDto test3 = createSong("test-3");
////
////
////        // when
////        Pageable pageRequest = PageRequest.of(0, 2);
////        Slice<SongSummaryDto> firstPage = beatmarketCrudFacade.findAllSongs(pageRequest);
////
////        // then
////        assertThat(firstPage.getContent())
////                .extracting(SongSummaryDto::id)
////                .containsExactlyInAnyOrder(test1.id(), test2.id());
////
////
////        assertTrue(firstPage.hasNext());
////
////        // when
////        pageRequest = PageRequest.of(1, 2);
////        Slice<SongSummaryDto> secondPage = beatmarketCrudFacade.findAllSongs(pageRequest);
////
////        // then
////        assertThat(secondPage.getContent())
////                .extracting(SongSummaryDto::id)
////                .containsExactly(test3.id());
////        assertFalse(secondPage.hasNext());
////    }
////
////    @Test
////    @DisplayName("Should return Slice<LegacySongDto> with one object")
////    public void should_return_one_song() {
////        //given
////        LegacySongDto test = createSong("test");
////        LegacyAlbumDto testAlbum = createAlbum("test", test.id());
////        //when
////        Slice<LegacyAlbumDto> all = beatmarketCrudFacade.findAllAlbums(Pageable.unpaged());
////        //then
////        assertThat(all)
////                .containsExactly(testAlbum);
////    }
////
////    @Test
////    @DisplayName("Should return Slice<LegacySongDto> with two objects")
////    public void should_return_two_songs() {
////        //given
////        LegacySongDto test1 = createSong("test-1");
////        LegacySongDto test2 = createSong("test-2");
////        //when
////        Slice<SongSummaryDto> all = beatmarketCrudFacade.findAllSongs(Pageable.unpaged());
////        //then
////        assertThat(all.getContent())
////                .extracting(SongSummaryDto::id)
////                .containsExactlyInAnyOrder(test1.id(), test2.id());
////    }
////
////    @Test
////    @DisplayName("Should return Slice<SongSummeryDto> with zero objects")
////    public void should_return_zero_songs() {
////        //when
////        Slice<SongSummaryDto> all = beatmarketCrudFacade.findAllSongs(Pageable.unpaged());
////        //then
////        assertThat(all)
////                .isEmpty();
////    }
////
////    //    public LegacySongDto findSongDtoById(Long id)
////

////
////    //    public Slice<LegacyAlbumDto> findAllAlbums(Pageable pageable)
////    @Test
////    @DisplayName("Should return correct Slice<LegacyAlbumDto> when pagination is applied")
////    public void should_return_correct_slice_AlbumDto_with_pagination() {
////        // given
////        LegacySongDto testS1 = createSong("test-1");
////        LegacySongDto testS2 = createSong("test-2");
////        LegacySongDto testS3 = createSong("test-3");
////
////        LegacyAlbumDto test1 = createAlbum("test-album-1", testS1.id());
////        LegacyAlbumDto test2 = createAlbum("test-album-2", testS2.id());
////        LegacyAlbumDto test3 = createAlbum("test-album-3", testS3.id());
////
////        // when
////        Pageable pageRequest = PageRequest.of(0, 2);
////        Slice<LegacyAlbumDto> firstPage = beatmarketCrudFacade.findAllAlbums(pageRequest);
////
////        // then
////        assertThat(firstPage)
////                .containsExactlyInAnyOrder(test1, test2);
////        assertTrue(firstPage.hasNext());
////
////        // when
////        pageRequest = PageRequest.of(1, 2);
////        Slice<LegacyAlbumDto> secondPage = beatmarketCrudFacade.findAllAlbums(pageRequest);
////
////        // then
////        assertThat(secondPage)
////                .containsExactly(test3);
////        assertFalse(secondPage.hasNext());
////    }
////
////    @Test
////    @DisplayName("Should return Slice<LegacyAlbumDto> with one object")
////    public void should_return_one_album() {
////        //given
////        LegacySongDto test = createSong("test");
////        //when
////        Slice<SongSummaryDto> allSongs = beatmarketCrudFacade.findAllSongs(Pageable.unpaged());
////        //then
////        assertThat(allSongs.getContent())
////                .extracting(SongSummaryDto::id)
////                .containsExactly(test.id());
////    }
//
//    @Test
//    @DisplayName("Should return Slice<LegacyAlbumDto> with two objects")
//    public void should_return_two_albums() {
//        //given
//        LegacySongDto test1 = createSong("test-1");
//        LegacySongDto test2 = createSong("test-2");
//        LegacyAlbumDto testAlbum1 = createAlbum("test", test1.id());
//        LegacyAlbumDto testAlbum2 = createAlbum("test", test2.id());
//
//        //when
//        Slice<LegacyAlbumDto> all = beatmarketCrudFacade.findAllAlbums(Pageable.unpaged());
//        //then
//        assertThat(all)
//                .containsExactlyInAnyOrder(testAlbum1, testAlbum2);
//    }
//
//    @Test
//    @DisplayName("Should return Slice<LegacyAlbumDto> with zero objects")
//    public void should_return_zero_albums() {
//        //when
//        Slice<LegacyAlbumDto> all = beatmarketCrudFacade.findAllAlbums(Pageable.unpaged());
//        //then
//        assertThat(all)
//                .isEmpty();
//    }
//
//    //    public Slice<LegacyGenreDto> findAllGenres(Pageable pageable)
//
//    @Test
//    @DisplayName("Should return correct Slice<LegacyGenreDto> when pagination is applied")
//    public void should_return_correct_slice_GenreDto_with_pagination() {
//        // given
//        LegacyGenreDto test1 = createGenre("test1");
//        LegacyGenreDto test2 = createGenre("test2");
//        LegacyGenreDto defaultGenre = beatmarketCrudFacade.findGenreById(1L);
//
//        // when
//        Pageable pageRequest = PageRequest.of(0, 2);
//        Slice<LegacyGenreDto> firstPage = beatmarketCrudFacade.findAllGenres(pageRequest);
//
//        // then
//        assertThat(firstPage)
//                .containsExactlyInAnyOrder(defaultGenre, test1);
//        assertTrue(firstPage.hasNext());
//
//        // when
//        pageRequest = PageRequest.of(1, 2);
//        Slice<LegacyGenreDto> secondPage = beatmarketCrudFacade.findAllGenres(pageRequest);
//
//        // then
//        assertThat(secondPage)
//                .containsExactly(test2);
//        assertFalse(secondPage.hasNext());
//    }
//
//    @Test
//    @DisplayName("Should return Slice<LegacyGenreDto> with two objects")
//    public void should_return_two_genre() {
//        //given
//        LegacyGenreDto test1 = createGenre("test1");
//        //when
//        Slice<LegacyGenreDto> all = beatmarketCrudFacade.findAllGenres(Pageable.unpaged());
//        //then
//        assertThat(all)
//                .contains(test1)
//                .hasSize(2);
//    }
//
//    @Test
//    @DisplayName("Should return Slice<LegacyGenreDto> with three objects")
//    public void should_return_two_genres() {
//        //given
//        LegacyGenreDto test1 = createGenre("test1");
//        LegacyGenreDto test2 = createGenre("test2");
//        //when
//        Slice<LegacyGenreDto> all = beatmarketCrudFacade.findAllGenres(Pageable.unpaged());
//        //then
//        assertThat(all)
//                .contains(test1, test2)
//                .hasSize(3);
//
//    }
//
//    @Test
//    @DisplayName("Should return Slice<LegacyGenreDto> with one object, Default Genre")
//    public void should_return_zero_genres() {
//        //when
//        Slice<LegacyGenreDto> all = beatmarketCrudFacade.findAllGenres(Pageable.unpaged());
//        //then
//        assertThat(all)
//                .extracting(LegacyGenreDto::name)
//                .contains("Default");
//    }
//
//    //    public LegacyArtistDto addArtistWithDefaultAlbumAndSong(ArtistRequestDto dto) {
//    @Test
//    @DisplayName("Should add Artist with default values for Album and Song")
//    public void should_add_artist_with_default_values_for_album_and_song() {
//        //given
//        assertThat(beatmarketCrudFacade.findAllArtists(Pageable.unpaged()).getContent()).isEmpty();
//        ArtistRequestDto requestDto = ArtistRequestDto.builder()
//                .name("test")
//                .build();
//        //when
//        LegacyArtistDto artistDto = beatmarketCrudFacade.addArtistWithDefaultAlbumAndSong(requestDto);
//        //then
//        Set<LegacyAlbumDto> albumsDtoByArtistId = beatmarketCrudFacade.findAlbumsDtoByArtistId(artistDto.id());
//        assertThat(albumsDtoByArtistId).extracting(LegacyAlbumDto::title).anyMatch(s -> s.startsWith("Default"));
//        assertThat(albumsDtoByArtistId).extracting(LegacyAlbumDto::title).anyMatch(s -> s.startsWith("Default"));
//    }
//
//    //    public LegacyGenreDto addGenre(SaveGenreDto dto) {
//
//    @ParameterizedTest
//    @DisplayName("Should throw NameIsBlankException when invalid name for Genre was sent")
//    @NullAndEmptySource
//    @ValueSource(strings = {"  ", "\t", "\n"})
//    void should_throw_exception_when_invalid_name_for_genre(String invalidName) {
//        // given
//        assertThat(beatmarketCrudFacade.findAllGenres(Pageable.unpaged()).getContent())
//                .hasSize(1);// there always is the default genre in db
//        SaveGenreDto genreRequestDto = new SaveGenreDto(invalidName);
//
//        // when & then
//        assertThatThrownBy(() -> beatmarketCrudFacade.addGenre(genreRequestDto))
//                .isInstanceOf(NameIsBlankException.class)
//                .hasMessage("Genre needs a specified name!");
//    }
//
//    @Test
//    @DisplayName("Should add genre")
//    void should_add_genre() {
//        // given
//        assertThat(beatmarketCrudFacade.findAllGenres(Pageable.unpaged()).getContent())
//                .hasSize(1);// there always is the default genre in db
//        SaveGenreDto genreRequestDto = new SaveGenreDto("test");
//
//        // when
//        LegacyGenreDto genreDto = beatmarketCrudFacade.addGenre(genreRequestDto);
//
//        // then
//        Slice<LegacyGenreDto> allGenres = beatmarketCrudFacade.findAllGenres(Pageable.unpaged());
//        assertThat(allGenres)
//                .contains(genreDto)
//                .hasSize(2);
//    }
//
//    //    public LegacyAlbumDto addAlbumWithSong(AlbumRequestDto dto) {
//
//    @Test
//    @DisplayName("Should add album with song")
//    void should_add_album_with_song() {
//        // given
//        LegacySongDto songDto = createSong("song1");
//        assertThat(beatmarketCrudFacade.findAllAlbums(Pageable.unpaged())).isEmpty();
//
//        AlbumRequestDto albumRequest = AlbumRequestDto.builder()
//                .title("album1")
//                .songId(songDto.id())
//                .releaseDate(LocalDate.now())
//                .build();
//
//        // when
//        LegacyAlbumDto albumDto = beatmarketCrudFacade.addAlbumWithSong(albumRequest);
//
//        // then
//        assertThat(beatmarketCrudFacade.findAllAlbums(Pageable.unpaged()).getContent()).hasSize(1);
//        AlbumInfo albumWithSongs = beatmarketCrudFacade.findAlbumByIdReturnAlbumInfo(albumDto.id());
//        assertTrue(albumWithSongs.getSongs().stream().anyMatch(song -> song.getId().equals(songDto.id())));
//    }
//
//    @ParameterizedTest
//    @DisplayName("Should throw NameIsBlankException when invalid name for Artist was sent")
//    @NullAndEmptySource
//    @ValueSource(strings = {"  ", "\t", "\n"})
//    void should_throw_exception_when_invalid_title_for_album(String invalidName) {
//        // given
//        LegacySongDto songDto = createSong("song1");
//        assertThat(beatmarketCrudFacade.findAllAlbums(Pageable.unpaged())).isEmpty();
//
//        AlbumRequestDto albumRequest = AlbumRequestDto.builder()
//                .title(invalidName)
//                .songId(songDto.id())
//                .releaseDate(LocalDate.now())
//                .build();
//
//        // when & then
//        assertThatThrownBy(() -> beatmarketCrudFacade.addAlbumWithSong(albumRequest))
//                .isInstanceOf(TitleIsBlankException.class)
//                .hasMessage("Album needs a specified title!");
//        assertThat(beatmarketCrudFacade.findAllAlbums(Pageable.unpaged())).isEmpty();
//    }
//
//    //    public ArtistWithAlbumDto addArtistToAlbum(Long artistId, Long albumId) {
//
//    @Test
//    @DisplayName("Should add Artist to Album")
//    void should_add_artist_to_album() {
//        // given
//        LegacyArtistDto artistDto = createArtist("test-artist");
//        LegacyAlbumDto albumDto = createAlbumWithNewSong("test-album", "test-song");
//
//        assertThat(beatmarketCrudFacade.findAllAlbums(Pageable.unpaged()).getContent()).hasSize(1);
//        assertThat(beatmarketCrudFacade.findAlbumsDtoByArtistId(artistDto.id())).isEmpty();
//
//        // when
//        beatmarketCrudFacade.addArtistToAlbum(artistDto.id(), albumDto.id());
//
//        // then
//        Set<LegacyAlbumDto> albumsDtoByArtistId = beatmarketCrudFacade.findAlbumsDtoByArtistId(artistDto.id());
//        assertThat(albumsDtoByArtistId)
//                .hasSize(1)
//                .extracting(LegacyAlbumDto::id)
//                .containsExactly(albumDto.id());
//    }
////        @TODO Fix after implementing filtering for all songs

//
//    //    public LegacyArtistDto addArtist(ArtistRequestDto dto)
//
//    @ParameterizedTest
//    @DisplayName("Should throw NameIsBlankException when invalid name for Artist was sent")
//    @NullAndEmptySource
//    @ValueSource(strings = {"  ", "\t", "\n"})
//    void should_throw_exception_when_invalid_name_for_artist(String invalidName) {
//        // given
//        ArtistRequestDto artistRequestDto = ArtistRequestDto.builder()
//                .name(invalidName)
//                .build();
//
//        // when & then
//        assertThatThrownBy(() -> beatmarketCrudFacade.addArtist(artistRequestDto))
//                .isInstanceOf(NameIsBlankException.class)
//                .hasMessage("Artist needs a specified name!");
//    }
//
//    @Test
//    @DisplayName("Should add artist 'bono' with id=0 when 'bono' was sent")
//    void should_add_artist_bono_with_id_zero_when_bono_was_sent() {
//        // given
//        assertThat(beatmarketCrudFacade.findAllArtists(Pageable.unpaged()).getContent()).isEmpty();
//        ArtistRequestDto bono = ArtistRequestDto.builder()
//                .name("bono")
//                .build();
//
//        // when
//        LegacyArtistDto result = beatmarketCrudFacade.addArtist(bono);
//
//        // then
//        assertThat(result.id()).isEqualTo(0L);
//        assertThat(result.name()).isEqualTo("bono");
//        assertThat(beatmarketCrudFacade.findAllArtists(Pageable.unpaged()).getContent()).hasSize(1);
//    }
//
//    //    public LegacyArtistDto updateArtistNameById(Long artistId, String name) {
//    @Test
//    @DisplayName("Should update Artist name")
//    public void should_update_artist_name() {
//        //given
//        LegacyArtistDto before = createArtist("before");
//
//        //when
//        LegacyArtistDto after = beatmarketCrudFacade.updateArtistNameById(before.id(), "after");
//
//        //then
//        assertThat(before.id()).isEqualTo(after.id());
//        assertThat(after.name()).isEqualTo("after");
//    }
//
//    @Test
//    @DisplayName("Should throw ArtistNotFoundException when updating non-existing artist")
//    public void should_throw_exception_when_updating_non_existing_artist() {
//        // given
//        Long nonExistingId = 999L;
//
//        // when & then
//        assertThatThrownBy(() -> beatmarketCrudFacade.updateArtistNameById(nonExistingId, "new-name"))
//                .isInstanceOf(ArtistNotFoundException.class)
//                .hasMessageContaining("Artist by id=" + nonExistingId);
//    }
//
//    //    public void deleteSongById(Long id) {
////   @TODO Fix after implementing filtering for all songs

//
//
//    //    public void deleteArtistByIdWithAlbumsAndSongs(Long artistId) {
//
//    @Test
//    @DisplayName("Should throw ArtistNotFoundException when id=0")
//    void should_throw_exception_artist_not_found_when_id_was_zero() {
//        // given
//        assertThat(beatmarketCrudFacade.findAllArtists(Pageable.unpaged())).isEmpty();
//
//        // when
//        Throwable throwable = catchThrowable(() -> beatmarketCrudFacade.deleteArtistByIdWithAlbumsAndSongs(0L));
//
//        // then
//        assertThat(throwable)
//                .isInstanceOf(ArtistNotFoundException.class)
//                .hasMessage("Artist by id=0 not found");
//    }
//
//    @Test
//    @DisplayName("Should delete Artist when it has no Album")
//    void should_delete_artist_when_no_albums() {
//        // given
//        LegacyArtistDto artistDto = createArtist("bono");
//        assertThat(beatmarketCrudFacade.findAlbumsDtoByArtistId(artistDto.id())).isEmpty();
//
//        // when
//        beatmarketCrudFacade.deleteArtistByIdWithAlbumsAndSongs(artistDto.id());
//
//        // then
//        assertThat(beatmarketCrudFacade.findAllArtists(Pageable.unpaged())).isEmpty();
//    }
//
//    @Test
//    @DisplayName("Should delete Artist by Id when Artist has 1 Album and was the only one on that Album")
//    void should_delete_artist_by_id_when_has_one_album_and_is_only_artist_on_given_album() {
//        // given
//        LegacyArtistDto artistDto = createArtist("bono");
//        LegacySongDto songDto = createSong("song1");
//        LegacyAlbumDto albumDto = createAlbum("album1", songDto.id());
//
//        beatmarketCrudFacade.addArtistToAlbum(artistDto.id(), albumDto.id());
//        assertThat(beatmarketCrudFacade.findAlbumsDtoByArtistId(artistDto.id())).hasSize(1);
//
//        // when
//        beatmarketCrudFacade.deleteArtistByIdWithAlbumsAndSongs(artistDto.id());
//
//        // then
//        assertThat(beatmarketCrudFacade.findAllArtists(Pageable.unpaged())).isEmpty();
//        assertThatThrownBy(() -> beatmarketCrudFacade.findAlbumsDtoByArtistId(artistDto.id()))
//                .isInstanceOf(ArtistNotFoundException.class)
//                .hasMessage("Artist by id=0 wasn't found in the database");
//
//        assertThatThrownBy(() -> beatmarketCrudFacade.findSongDtoById(songDto.id()))
//                .isInstanceOf(SongNotFoundException.class)
//                .hasMessage("Song with id 0 not found");
//    }
//
//    @Test
//    @DisplayName("Should remove Artist from Album when Album has 2+ Artists")
//    void should_only_remove_artist_from_album_when_more_than_two_artists() {
//        // given
//        LegacyArtistDto artist1 = createArtist("artist-1");
//        LegacyArtistDto artist2 = createArtist("artist-2");
//        assertThat(beatmarketCrudFacade.findAllArtists(Pageable.unpaged()).getContent()).hasSize(2);
//
//        LegacyAlbumDto album1 = createAlbumWithNewSong("album-1", "song-1");
//        beatmarketCrudFacade.addArtistToAlbum(artist1.id(), album1.id());
//        beatmarketCrudFacade.addArtistToAlbum(artist2.id(), album1.id());
//
//        Set<LegacyAlbumDto> albumsByArtist1 = beatmarketCrudFacade.findAlbumsDtoByArtistId(artist1.id());
//        Set<LegacyAlbumDto> albumsByArtist2 = beatmarketCrudFacade.findAlbumsDtoByArtistId(artist2.id());
//        assertThat(albumsByArtist1).isEqualTo(albumsByArtist2);
//
//        LegacyAlbumDto album2 = createAlbumWithNewSong("album-2", "song-2");
//        beatmarketCrudFacade.addArtistToAlbum(artist1.id(), album2.id());
//
//        assertThat(beatmarketCrudFacade.findAlbumsDtoByArtistId(artist1.id())).hasSize(2);
//
//        // when
//        beatmarketCrudFacade.deleteArtistByIdWithAlbumsAndSongs(artist1.id());
//
//        // then
//        assertThatThrownBy(() -> beatmarketCrudFacade.findAlbumsDtoByArtistId(artist1.id()))
//                .isInstanceOf(ArtistNotFoundException.class)
//                .hasMessage("Artist by id=0 wasn't found in the database");
//        assertThat(beatmarketCrudFacade.findAlbumsDtoByArtistId(artist2.id()))
//                .hasSize(1)
//                .extracting(LegacyAlbumDto::id)
//                .contains(album1.id());
//    }
//
//    //public LegacyGenreDto findGenreById(final Long genreId)
//
//    @Test
//    @DisplayName("Should retrieve Genre by ID")
//    public void should_find_genre_by_id() {
//        //given
//        LegacyGenreDto test = createGenre("test");
//        assertThat(beatmarketCrudFacade.findAllGenres(Pageable.unpaged())).hasSize(2);
//        //when
//        LegacyGenreDto genreById = beatmarketCrudFacade.findGenreById(test.id());
//        //then
//        assertThat(genreById).isEqualTo(test);
//        //when
//        LegacyGenreDto defaultGenreById = beatmarketCrudFacade.findGenreById(1L);
//        //then
//        assertThat(defaultGenreById.id()).isEqualTo(1L);
//        assertThat(defaultGenreById.name()).isEqualTo("Default");
//    }
//
//    @Test
//    @DisplayName("Should throw GenreNotFoundException when Genre doesn't exist")
//    public void should_throw_exception_when_genre_doesnt_exist() {
//        //given
//        Long nonExistingGenreId = 999L;
//        //when & then
//        assertThatThrownBy(() -> beatmarketCrudFacade.findGenreById(nonExistingGenreId))
//                .isInstanceOf(GenreNotfoundException.class)
//                .hasMessage("Genre by id=" + nonExistingGenreId + "wasn't found");
//    }
//
////    public LegacySongDto updateSongPartiallyById(Long id, LegacySongDto songFromRequest) {
//

//
////    public int deleteGenreById(final Long genreId) {
//
//    @Test
//    @DisplayName("Should delete Genre By Id")
//    public void should_delete_genre_by_id(){
//        //given
//        Long genreId = createGenre("test").id();
//        assertThat(beatmarketCrudFacade.findAllGenres(Pageable.unpaged()).getSize()).isEqualTo(2);
//        //when & then
//        assertThat(beatmarketCrudFacade.deleteGenreById(genreId)).isEqualTo(1);
//        assertThat(beatmarketCrudFacade.findAllGenres(Pageable.unpaged()).getSize()).isEqualTo(1);
//    }
//
//    @Test
//    @DisplayName("Should throw exception when deleting Genre By nonexistent Id")
//    public void should_not_delete_genre_by_id(){
//        //given
//        Long nonExistent = 999L;
//        assertThat(beatmarketCrudFacade.findAllGenres(Pageable.unpaged()).getSize()).isEqualTo(1);
//        //when & then
//        assertThatThrownBy( () -> beatmarketCrudFacade.deleteGenreById(nonExistent))
//                .isInstanceOf(GenreNotfoundException.class)
//                .hasMessage("Genre by id=999 was not found");
//    }
//
//    //    public LegacySongDto assignGenreByIdToSongById(final Long songId, final Long genreId) {
//    @Test
//    @DisplayName("Should assign Genre By id=2 to Song by id=0")
//    public void should_assign_genre_by_id_to_song_by_id(){
//        //given
//        LegacySongDto song = createSong("song");
//        LegacyGenreDto genre = createGenre("genre");
//        assertThat(song.genre().id()).isNotEqualTo(genre.id());
//        //when
//        LegacySongDto updated = beatmarketCrudFacade.assignGenreByIdToSongById(song.id(), genre.id());
//        //then
//        assertThat(updated.genre().id()).isEqualTo(genre.id());
//    }
//
//    // public AlbumSongsDto assignSongByIdToAlbumById(final Long albumId, final Long songId) {
//    @Test
//    @DisplayName("Should assign Song By id=0 to Album by id=0")
//    public void should_assign_song_by_id_to_album_by_id(){
//        //given
//        LegacySongDto song1 = createSong("song1");
//        LegacySongDto song2 = createSong("song2");
//        LegacyAlbumDto album = createAlbum("album", song1.id());
//        Set<AlbumInfo.SongInfo> songsBefore = beatmarketCrudFacade.findAlbumByIdReturnAlbumInfo(album.id()).getSongs();
//        assertThat(songsBefore.size()).isEqualTo(1);
//        //when
//        beatmarketCrudFacade.assignSongByIdToAlbumById(album.id(), song2.id());
//        //then
//        Set<AlbumInfo.SongInfo> songsAfter = beatmarketCrudFacade.findAlbumByIdReturnAlbumInfo(album.id()).getSongs();
//        Set<Long> songIdsAfter = songsAfter.stream().map(AlbumInfo.SongInfo::getId).collect(Collectors.toSet());
//        assertThat(songIdsAfter.size())
//                .isEqualTo(2);
//        assertThat(songIdsAfter)
//                .contains(song1.id(), song2.id());
//    }
//
//
//        // --- HELPER METHODS ---
//
//    private LegacyArtistDto createArtist(String name) {
//        ArtistRequestDto request = ArtistRequestDto.builder()
//                .name(name)
//                .build();
//        return beatmarketCrudFacade.addArtist(request);
//    }
//
//    private LegacyGenreDto createGenre(String name) {
//        SaveGenreDto request = new SaveGenreDto(name);
//        return beatmarketCrudFacade.addGenre(request);
//    }
//
//    private SongCreatedDto createSong(String name) {
//        CreateSongDto request = CreateSongDto.builder()
//                .title(name)
//                .language(SongLanguage.EN)
//                .duration(100L)
//                .releaseDate(LocalDate.now())
//                .build();
//        return beatmarketCrudFacade.addSong(request);
//    }
//
//
//
//    private LegacyAlbumDto createAlbum(String title, Long songId) {
//        AlbumRequestDto request = AlbumRequestDto.builder()
//                .title(title)
//                .songId(songId)
//                .releaseDate(LocalDate.now())
//                .build();
//        return beatmarketCrudFacade.addAlbumWithSong(request);
//    }
//
//    private LegacyAlbumDto createAlbumWithNewSong(String albumTitle, String songName) {
//        LegacySongDto song = createSong(songName);
//        return createAlbum(albumTitle, song.id());
//    }
//}