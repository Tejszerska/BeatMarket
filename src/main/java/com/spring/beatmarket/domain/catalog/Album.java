package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.exception.MissingRequiredFieldException;
import com.spring.beatmarket.domain.shared.domain.BaseEntity;
import jakarta.persistence.CascadeType;
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
    private String coverFileKey;

    @ManyToMany
    @JoinTable(
            name = "album_artist",
            joinColumns = @JoinColumn(name = "album_id"),
            inverseJoinColumns = @JoinColumn(name = "artist_id")
    )
    @OrderColumn(name = "artist_order")
    private List<Artist> artists = new ArrayList<>();

    @OneToMany(mappedBy = "album",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Song> songs = new HashSet<>();

    @Builder
    Album(final String title, final LocalDate releaseDate, final String coverFileKey, final List<Artist> artists, final Set<Song> songs) {
        if (title == null || title.isBlank()) {
            throw new MissingRequiredFieldException("title");
        }
        if (releaseDate != null && releaseDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Release date can't be in the future");
        }

        this.title = title;
        this.releaseDate = releaseDate;
        this.coverFileKey = coverFileKey;
        this.artists = artists != null ? new ArrayList<>(artists) : new ArrayList<>();
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
        this.coverFileKey = newCoverUrl;
    }
    // Business validation (e.g., enforcing main vs. featured artist constraints)
    // is delegated to the ArtistRoleManager. This method solely handles persistence sync.
    void assignArtist(Artist artist, boolean isMain) {
        if (artist == null) return;

        boolean wasRemoved = this.artists.remove(artist);

        if (wasRemoved) artist.removeAlbum(this);

        if (isMain) {
            // Domain rule: The Main Artist is always strictly maintained at index 0
            this.artists.add(0, artist);
        } else {
            this.artists.add(artist);
        }

        artist.getAlbums().add(this);
    }

    void removeArtist(Artist artist) {
        if (artist != null && this.artists.contains(artist)) {
            this.artists.remove(artist);
            if (artist.getAlbums().contains(this)) {
                artist.removeAlbum(this);
            }
        }
    }


    // Re-creating the collection prevents a Constraint Violation exception
    // when Hibernate attempts to reorder artists (e.g., switching main and featured roles).
    void clearArtists() {
        if (!this.artists.isEmpty()) {
            this.artists.forEach(artist -> artist.removeAlbum(this));
            this.artists = new ArrayList<>();
        }
    }

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

    void clearSongs() {
        Set<Song> songsToRemove = new HashSet<>(this.songs);
        songsToRemove.forEach(this::removeSong);
    }

}