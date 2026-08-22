package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.exception.MissingRequiredFieldException;
import com.spring.beatmarket.domain.shared.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.SequenceGenerator;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class Album extends BaseEntity {

    @Id
    @GeneratedValue(generator = "album_id_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(
            name = "album_id_seq",
            sequenceName = "album_id_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(nullable = false)
    private String title;

    private LocalDate releaseDate;

    @Column(columnDefinition = "TEXT")
    private String coverUrl;

    @ManyToMany
    @JoinTable(
            name = "album_artist",
            joinColumns = @JoinColumn(name = "album_id"),
            inverseJoinColumns = @JoinColumn(name = "artist_id")
    )
    @OrderColumn(name = "artist_order")
    private List<Artist> artists = new ArrayList<>();

    @OneToMany(mappedBy = "album")
    private Set<Song> songs = new HashSet<>();

    Album(final String title, final LocalDate releaseDate) {
        this(title, releaseDate, null, new ArrayList<>(), new HashSet<>());
    }

    /**
     * Ensures structural integrity by enforcing non-null constraints and
     * eagerly initializing relationships to avoid NullPointerExceptions.
     */
    @Builder
    Album(final String title, final LocalDate releaseDate, final String coverUrl, final List<Artist> artists, final Set<Song> songs) {
        if (title == null || title.isBlank()) {
            throw new MissingRequiredFieldException("title");
        }
        if (releaseDate != null && releaseDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Release date can't be in the future");
        }

        this.title = title;
        this.releaseDate = releaseDate;
        this.coverUrl = coverUrl;
        this.artists = artists != null ? artists : new ArrayList<>();
        this.songs = songs != null ? songs : new HashSet<>();
    }

    void changeTitle(String newTitle) {
        if (newTitle == null || newTitle.isBlank()) {
            throw new MissingRequiredFieldException("title");
        }
        this.title = newTitle;
    }

    void changeReleaseDate(LocalDate newReleaseDate) {
        if (newReleaseDate != null && newReleaseDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Release date can't be in the future");
        }
        this.releaseDate = newReleaseDate;
    }

    void changeCoverUrl(String newCoverUrl) {
        this.coverUrl = newCoverUrl;
    }

    /**
     * Manages the owning side of the ManyToMany relationship with Artist.
     * Safely updates both entities to keep the Persistence Context synchronized.
     */
    void addArtist(Artist artist) {
        if (artist != null && !this.artists.contains(artist)) {
            this.artists.add(artist);
            if (!artist.getAlbums().contains(this)) {
                artist.addAlbum(this);
            }
        }
    }

    void removeArtist(Artist artist) {
        if (artist != null && this.artists.contains(artist)) {
            this.artists.remove(artist);
            if (artist.getAlbums().contains(this)) {
                artist.removeAlbum(this);
            }
        }
    }

    void changeArtistsList(List<Artist> newArtists) {
        this.artists.clear();
        if (newArtists != null) {
            newArtists.forEach(this::addArtist);
        }
    }

    void clearArtists() {
        List<Artist> artistsToRemove = new ArrayList<>(this.artists);
        artistsToRemove.forEach(this::removeArtist);
    }

    /**
     * Manages the inverse side of the OneToMany relationship with Song.
     */
    void addSong(Song song) {
        if (song != null && !this.songs.contains(song)) {
            this.songs.add(song);
            if (song.getAlbum() != this) {
                song.assignToAlbum(this);
            }
        }
    }

    void removeSong(Song song) {
        if (song != null && this.songs.contains(song)) {
            this.songs.remove(song);
            if (song.getAlbum() == this) {
                song.detachFromAlbum();
            }
        }
    }

    void changeSongsList(Set<Song> newSongs) {
        this.songs.clear();
        if (newSongs != null) {
            newSongs.forEach(this::addSong);
        }
    }

    void clearSongs() {
        Set<Song> songsToRemove = new HashSet<>(this.songs);
        songsToRemove.forEach(this::removeSong);
    }

    //     --------- legacy versions -----------
//    void addSongToAlbum(final Song song) {
//        songs.add(song);
//    }
//
//    void removeArtist(Artist artist){
//        artists.remove(artist);
//        artist.removeAlbum(this);
//    }
//
//    void addArtist(final Artist artist) {
//        artists.add(artist);
//    }
//
//    void assignDefaultTitle(){
//        this.title = "Default album:" + this.uuid.toString();
//    }
}