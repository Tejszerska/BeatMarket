package com.spring.beatmarket.domain.catalog;

import org.hibernate.Hibernate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AlbumRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private AlbumRepository albumRepository;

    @Test
    @DisplayName("Should save Album and retrieve it")
    void should_save_and_retrieve_album() {
        // given
        Album savedAlbum = persister.createAndSaveAlbum("Album");
        flushAndClear();

        // when
        Optional<Album> retrieved = albumRepository.findByIdAndActiveTrue(savedAlbum.getId());

        // then
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getTitle()).isEqualTo("Album");
    }

    @Test
    @DisplayName("Should return empty Optional when Album is inactive")
    void should_return_empty_optional_when_inactive() {
        // given
        Album savedAlbum = persister.createAndSaveAlbum("Album");
        savedAlbum.deactivate();
        flushAndClear();

        // when
        Optional<Album> retrieved = albumRepository.findByIdAndActiveTrue(savedAlbum.getId());

        // then
        assertThat(retrieved).isEmpty();
    }

    @Test
    @DisplayName("Should return Slice with 3 albums when no filtering")
    void should_return_slice_with_3() {
        //given
        Album album1 = persister.createAndSaveAlbum("Title1");
        Artist artist = persister.createAndSaveArtist("Artist");
        Album album2 = persister.createAndSaveAlbumWithArtists("Title2", List.of(artist));
        Album album3 = persister.createAndSaveAlbum("Different");

        Album album4 = persister.createAndSaveAlbum("Title4");
        album4.deactivate();

        flushAndClear();
        Pageable pageable = Pageable.ofSize(5);

        //when
        Slice<Album> albumSlice = albumRepository.findByActiveTrue(pageable);

        //then
        assertThat(albumSlice.getContent())
                .containsExactlyInAnyOrder(album1, album2, album3);
        assertThat(albumSlice.getContent()).doesNotContain(album4);
    }

    @Test
    @DisplayName("Should return Slice with 2 albums when filtering by title")
    void should_return_slice_with_2_by_title() {
        //given
        Album album1 = persister.createAndSaveAlbum("Title1");
        Artist artist = persister.createAndSaveArtist("Artist");
        Album album2 = persister.createAndSaveAlbumWithArtists("Title2", List.of(artist));
        Album album3 = persister.createAndSaveAlbum("Different");

        Album album4 = persister.createAndSaveAlbum("Title4");
        album4.deactivate();

        flushAndClear();
        Pageable pageable = Pageable.ofSize(5);

        //when
        Slice<Album> albumSlice = albumRepository.findByActiveTrueAndTitleContainingIgnoreCase("tiT", pageable);

        //then
        assertThat(albumSlice.getContent())
                .containsExactlyInAnyOrder(album1, album2);
    }

    @Test
    @DisplayName("Should return Slice with 2 albums when filtering by artist")
    void should_return_slice_with_2_by_artist_id() {
        //given
        Album album1 = persister.createAndSaveAlbum("Title1");
        Artist savedArtist = persister.createAndSaveArtist("Artist");
        Album album2 = persister.createAndSaveAlbumWithArtists("Title2", List.of(savedArtist));
        Album album3 = persister.createAndSaveAlbumWithArtists("Different", List.of(savedArtist));

        Album album4 = persister.createAndSaveAlbum("Title4");
        album4.deactivate();

        flushAndClear();
        Pageable pageable = Pageable.ofSize(5);

        //when
        Slice<Album> albumSlice = albumRepository.findByActiveTrueAndArtists_Id(savedArtist.getId(), pageable);

        //then
        assertThat(albumSlice.getContent())
                .containsExactlyInAnyOrder(album3, album2);
    }

    @Test
    @DisplayName("Should return Slice with 1 album when filtering by artist id and title")
    void should_return_slice_with_1() {
        //given
        Album album1 = persister.createAndSaveAlbum("Title1");
        Artist savedArtist = persister.createAndSaveArtist("Artist");
        Album album2 = persister.createAndSaveAlbumWithArtists("Title2", List.of(savedArtist));
        Album album3 = persister.createAndSaveAlbumWithArtists("Different", List.of(savedArtist));

        Album album4 = persister.createAndSaveAlbum("Title4");
        album4.deactivate();

        flushAndClear();
        Pageable pageable = Pageable.ofSize(5);

        //when
        Slice<Album> albumSlice = albumRepository.findByActiveTrueAndArtists_IdAndTitleContainingIgnoreCase(savedArtist.getId(), "tit", pageable);

        //then
        assertThat(albumSlice.getContent())
                .containsExactly(album2);
    }

    @Test
    @DisplayName("Should return list of active albums with artists data (including deactivated)")
    void should_return_albums_with_artists() {
        // given
        Artist activeArtist = persister.createAndSaveArtist("Artist1");
        Artist inactiveArtist = persister.createAndSaveArtist("Artist2");
        inactiveArtist.deactivate();

        Album inactiveAlbum = persister.createAndSaveAlbumWithArtists("Title1", List.of(activeArtist));
        inactiveAlbum.deactivate();

        Album activeAlbumMulti = persister.createAndSaveAlbumWithArtists("Title2", List.of(activeArtist, inactiveArtist));
        Album activeAlbumSingle = persister.createAndSaveAlbumWithArtists("Title3", List.of(inactiveArtist));

        List<Long> ids = List.of(inactiveAlbum.getId(), activeAlbumMulti.getId(), activeAlbumSingle.getId());
        flushAndClear();

        // when
        List<Album> results = albumRepository.findActiveWithArtistsByIds(ids);

        // then
        assertThat(results)
                .extracting(Album::getId)
                .containsExactlyInAnyOrder(activeAlbumMulti.getId(), activeAlbumSingle.getId());

        assertThat(results).allSatisfy(album ->
                assertThat(Hibernate.isInitialized(album.getArtists())).isTrue()
        );

        assertThat(results)
                .filteredOn(album -> album.getId().equals(activeAlbumMulti.getId()))
                .singleElement()
                .satisfies(album -> assertThat(album.getArtists()).hasSize(2));
    }

    @Test
    @DisplayName("Should return an active albums found by id with all related data initialized (Song, Artist) including inactive")
    void should_return_albums_with_all() {
        // given
        Artist activeArtist = persister.createAndSaveArtist("Artist1");
        Artist inactiveArtist = persister.createAndSaveArtist("Artist2");
        inactiveArtist.deactivate();

        Album savedAlbum = persister.createAndSaveAlbumWithArtists("Title", List.of(activeArtist, inactiveArtist));

        persister.createAndSaveSong("Song1", savedAlbum);

        Song inactiveSong = persister.createAndSaveSong("Song2", savedAlbum);
        inactiveSong.deactivate();

        flushAndClear();

        // when
        Optional<Album> albumById = albumRepository.findAlbumByIdEagerly(savedAlbum.getId());

        // then
        assertThat(albumById).isPresent();
        assertThat(Hibernate.isInitialized(albumById.get().getArtists())).isTrue();
        assertThat(Hibernate.isInitialized(albumById.get().getSongs())).isTrue();

        assertThat((albumById.get().getArtists())).hasSize(2);
        assertThat((albumById.get().getSongs())).hasSize(2);
    }

    @Test
    @DisplayName("Should return empty Optional if album is inactive")
    void should_not_return_album() {
        // given
        Artist activeArtist = persister.createAndSaveArtist("Artist1");
        Album savedAlbum = persister.createAndSaveAlbumWithArtists("Title", List.of(activeArtist));
        savedAlbum.deactivate();

        persister.createAndSaveSong("Song1", savedAlbum);

        flushAndClear();

        // when
        Optional<Album> albumById = albumRepository.findAlbumByIdEagerly(savedAlbum.getId());

        // then
        assertThat(albumById).isEmpty();
    }

    @Test
    @DisplayName("Should deactivate in bulk setting version and editedOn correctly")
    void should_deactivate_in_bulk() {
        // given
        Album album1 = persister.createAndSaveAlbum("Title1");
        Long id1 = album1.getId();

        Album album2 = persister.createAndSaveAlbum("Title2");
        Long id2 = album2.getId();

        flushAndClear();

        Instant now = Instant.now().truncatedTo(ChronoUnit.MICROS);

        // when
        albumRepository.deactivateAllByIds(Set.of(id1, id2), now);

        // then
        assertThat(albumRepository.findByIdAndActiveTrue(id1)).isEmpty();
        assertThat(albumRepository.findByIdAndActiveTrue(id2)).isEmpty();

        Album updatedAlbum1 = entityManager.find(Album.class, id1);
        Album updatedAlbum2 = entityManager.find(Album.class, id2);

        assertThat(updatedAlbum1.isActive()).isFalse();
        assertThat(updatedAlbum1.getVersion()).isEqualTo(1L);
        assertThat(updatedAlbum1.getEditedOn()).isEqualTo(now);

        assertThat(updatedAlbum2.isActive()).isFalse();
        assertThat(updatedAlbum2.getVersion()).isEqualTo(1L);
        assertThat(updatedAlbum2.getEditedOn()).isEqualTo(now);
    }
}