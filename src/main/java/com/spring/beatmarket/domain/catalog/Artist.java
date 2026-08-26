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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

    @ManyToMany(mappedBy = "artists")
    private Set<Song> songs = new HashSet<>();

    @ManyToMany(mappedBy = "artists")
    private List<Album> albums = new ArrayList<>();

    Artist(final String name) {
        this(name, null, new HashSet<>(), new ArrayList<>());
    }

    @Builder
    Artist(final String name, final String imageUrl, final Set<Song> songs, final List<Album> albums) {
        if (name == null || name.isBlank()) {
            throw new MissingRequiredFieldException("name");
        }

        this.name = name;
        this.imageUrl = imageUrl;
        this.songs = songs != null ? songs : new HashSet<>();
        this.albums = albums != null ? albums : new ArrayList<>();
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
     * Passive side of the ManyToMany relationship with Album.
     * Persistence is managed by the Album entity. Use Album.assignArtist()
     * to properly establish database relationships.
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
     * Passive side of the ManyToMany relationship with Song.
     * Persistence is managed by the Song entity. Use Song.assignArtist()
     * to properly establish database relationships.
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

//    --------- legacy versions -----------
//    void removeAlbum(Album album){
//        albums.remove(album);
//    }
//
//    void addAlbum(final Album album) {
//        albums.add(album);
//        album.addArtist(this);
//    }
