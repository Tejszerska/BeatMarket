package com.spring.beatmarket.domain.catalog;

import org.hibernate.Hibernate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class AlbumRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        entityManager.getEntityManager()
                .createQuery("DELETE FROM Song")
                .executeUpdate();

        entityManager.getEntityManager()
                .createQuery("DELETE FROM Album")
                .executeUpdate();
    }

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private SongRepository songRepository;

    @Test
    @DisplayName("Should save Album and retrieve it")
    void should_save_and_retrieve_album() {
        // given
        Album album = Album.builder().title("Album").build();
        // when
        Album savedAlbum = albumRepository.save(album);

        entityManager.flush();
        entityManager.clear();

        // then
        Optional<Album> retrieved = albumRepository.findByIdAndActiveTrue(savedAlbum.getId());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getTitle()).isEqualTo("Album");
    }

    @Test
    @DisplayName("Should return empty Optional when Album is inactive")
    void should_return_empty_optional_when_inactive() {
        // given
        Album album = Album.builder().title("Album").build();

        Album savedAlbum = albumRepository.save(album);
        savedAlbum.deactivate();
        entityManager.flush();
        entityManager.clear();

        // when
        Optional<Album> retrieved = albumRepository.findByIdAndActiveTrue(savedAlbum.getId());

        // then
        assertThat(retrieved).isEmpty();
    }

    @Test
    @DisplayName("Should return Slice with 3 albums when no filtering")
    void should_return_slice_with_3() {
        //given
        Album album1 = Album.builder().title("Title1").build();
        albumRepository.save(album1);

        Artist artist = Artist.builder().name("Artist").build();
        artistRepository.save(artist);
        Album album2 = Album.builder().title("Title2").artists(List.of(artist)).build();
        albumRepository.save(album2);

        Album album3 = Album.builder().title("Different").build();
        albumRepository.save(album3);

        Album album4 = Album.builder().title("Title4").build();
        album4.deactivate();
        albumRepository.save(album4);

        entityManager.flush();
        entityManager.clear();
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
        Album album1 = Album.builder().title("Title1").build();
        albumRepository.save(album1);

        Artist artist = Artist.builder().name("Artist").build();
        artistRepository.save(artist);

        Album album2 = Album.builder().title("Title2").artists(List.of(artist)).build();
        albumRepository.save(album2);

        Album album3 = Album.builder().title("Different").build();
        albumRepository.save(album3);

        Album album4 = Album.builder().title("Title4").build();
        album4.deactivate();
        albumRepository.save(album4);

        entityManager.flush();
        entityManager.clear();
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
        Album album1 = Album.builder().title("Title1").build();
        albumRepository.save(album1);

        Artist artist = Artist.builder().name("Artist").build();
        Artist savedArtist = artistRepository.save(artist);

        Album album2 = Album.builder()
                .title("Title2")
                .artists(List.of(artist)).build();
        albumRepository.save(album2);

        Album album3 = Album.builder()
                .title("Different")
                .artists(List.of(artist)).build();
        albumRepository.save(album3);

        Album album4 = Album.builder().title("Title4").build();
        album4.deactivate();
        albumRepository.save(album4);

        entityManager.flush();
        entityManager.clear();
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
        Album album1 = Album.builder().title("Title1").build();
        albumRepository.save(album1);

        Artist artist = Artist.builder().name("Artist").build();
        Artist savedArtist = artistRepository.save(artist);

        Album album2 = Album.builder()
                .title("Title2")
                .artists(List.of(artist)).build();
        albumRepository.save(album2);

        Album album3 = Album.builder()
                .title("Different")
                .artists(List.of(artist)).build();
        albumRepository.save(album3);

        Album album4 = Album.builder().title("Title4").build();
        album4.deactivate();
        albumRepository.save(album4);

        entityManager.flush();
        entityManager.clear();
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
        Artist activeArtist = artistRepository.save(Artist.builder().name("Artist1").build());

        Artist inactiveArtist = Artist.builder().name("Artist2").build();
        inactiveArtist.deactivate();
        artistRepository.save(inactiveArtist);

        Album inactiveAlbum = Album.builder().title("Title1").artists(List.of(activeArtist)).build();
        inactiveAlbum.deactivate();
        albumRepository.save(inactiveAlbum);

        Album activeAlbumMulti = albumRepository.save(
                Album.builder().title("Title2").artists(List.of(activeArtist, inactiveArtist)).build()
        );

        Album activeAlbumSingle = albumRepository.save(
                Album.builder().title("Title3").artists(List.of(inactiveArtist)).build()
        );

        List<Long> ids = List.of(inactiveAlbum.getId(), activeAlbumMulti.getId(), activeAlbumSingle.getId());

        entityManager.flush();
        entityManager.clear();

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
        Artist activeArtist = artistRepository.save(Artist.builder().name("Artist1").build());

        Artist inactiveArtist = Artist.builder().name("Artist2").build();
        inactiveArtist.deactivate();
        artistRepository.save(inactiveArtist);

        Album savedAlbum = albumRepository.save(
                Album.builder()
                        .title("Title")
                        .artists(List.of(activeArtist, inactiveArtist))
                        .build()
        );


        Song activeSong = songRepository.save(Song.builder()
                .title("Song1")
                .releaseDate(LocalDate.now())
                .duration(200)
                .language(SongLanguage.EN)
                .album(savedAlbum)
                .build());

        Song inactiveSong = Song.builder()
                .title("Song2")
                .releaseDate(LocalDate.now())
                .duration(200)
                .language(SongLanguage.EN)
                .album(savedAlbum)
                .build();
        inactiveSong.deactivate();
        songRepository.save(inactiveSong);

        entityManager.flush();
        entityManager.clear();


        // when
        Optional<Album> albumById = albumRepository.findAlbumByIdEagerly(savedAlbum.getId());

        // then
        assertThat(albumById).isPresent();
        assertThat(Hibernate.isInitialized(albumById.get().getArtists()))
                .isTrue();
        assertThat(Hibernate.isInitialized(albumById.get().getSongs())).isTrue();

        assertThat((albumById.get().getArtists())).hasSize(2);
        assertThat((albumById.get().getSongs())).hasSize(2);
    }

    @Test
    @DisplayName("Should return empty Optional if album is inactive")
    void should_not_return_album() {
        // given
        Artist activeArtist = artistRepository.save(Artist.builder().name("Artist1").build());

        Album savedAlbum = albumRepository.save(
                Album.builder()
                        .title("Title")
                        .artists(List.of(activeArtist))
                        .build()
        );
        savedAlbum.deactivate();

        Song activeSong = songRepository.save(Song.builder()
                .title("Song1")
                .releaseDate(LocalDate.now())
                .duration(200)
                .language(SongLanguage.EN)
                .album(savedAlbum)
                .build());

        entityManager.flush();
        entityManager.clear();

        // when
        Optional<Album> albumById = albumRepository.findAlbumByIdEagerly(savedAlbum.getId());

        // then
        assertThat(albumById).isEmpty();
    }

    @Test
    @DisplayName("Should deactivate in bulk setting version and editedOn correctly")
    void should_deactivate_in_bulk() {
        // given
        Album album1 = albumRepository.save(Album.builder().title("Title1").build());
        Long id1 = album1.getId();

        Album album2 = albumRepository.save(Album.builder().title("Title2").build());
        Long id2 = album2.getId();

        entityManager.flush();
        entityManager.clear();

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
