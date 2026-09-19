package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.exception.MissingRequiredFieldException;
import com.spring.beatmarket.domain.shared.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.SequenceGenerator;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class Artist extends BaseEntity {

    @Id
    @GeneratedValue(generator = "artist_id_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(
            name = "artist_id_seq",
            sequenceName = "artist_id_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String imageUrl;

    // Using Set instead of List to prevent Hibernate MultipleBagFetchException
    // when multiple collections are fetched simultaneously.
    @ManyToMany(mappedBy = "artists")
    private Set<Song> songs = new HashSet<>();

    @ManyToMany(mappedBy = "artists")
    private Set<Album> albums = new HashSet<>();

    Artist(final String name) {
        this(name, null, new HashSet<>(), new HashSet<>());
    }

    @Builder
    Artist(final String name, final String imageUrl, final Set<Song> songs, final Set<Album> albums) {
        if (name == null || name.isBlank()) {
            throw new MissingRequiredFieldException("name");
        }

        this.name = name;
        this.imageUrl = imageUrl;
        this.songs = songs != null ? new HashSet<>(songs) : new HashSet<>();
        this.albums = albums != null ? new HashSet<>(albums) : new HashSet<>();
    }

    void changeName(String newName) {
        if (newName == null || newName.isBlank()) {
            throw new MissingRequiredFieldException("name");
        }
        this.name = newName.trim();
    }

    void changeImageUrl(String newImageUrl) {
        this.imageUrl = newImageUrl;
    }

    /**
     * Warning: Updates in-memory collection only.
     * To persist the relationship in the database, you must use {@link Album#assignArtist(Artist, boolean)}.
     */
    void addAlbum(Album album) {
        if (album != null){
            this.albums.add(album);
        }
    }

    void removeAlbum(Album album) {
        if (album != null) {
            this.albums.remove(album);
        }
    }

    /**
     * Warning: Updates in-memory collection only.
     * To persist the relationship in the database, you must use {@link Song#assignArtist(Artist, boolean)}.
     */
    void addSong(Song song) {
        if (song != null) {
            this.songs.add(song);
        }
    }

    void removeSong(Song song) {
        if (song != null) {
            this.songs.remove(song);
        }
    }

}