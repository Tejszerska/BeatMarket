CREATE SEQUENCE IF NOT EXISTS album_id_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS artist_id_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS genre_id_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS song_id_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS users_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE album
(
    id           BIGINT NOT NULL,
    uuid         UUID,
    created_on   TIMESTAMP WITHOUT TIME ZONE,
    version      BIGINT,
    title        VARCHAR(255),
    release_date TIMESTAMP WITHOUT TIME ZONE,
    cover_url    TEXT,
    CONSTRAINT pk_album PRIMARY KEY (id)
);

CREATE TABLE artist
(
    id         BIGINT       NOT NULL,
    uuid       UUID,
    created_on TIMESTAMP WITHOUT TIME ZONE,
    version    BIGINT,
    name       VARCHAR(255) NOT NULL,
    image_url  TEXT,
    CONSTRAINT pk_artist PRIMARY KEY (id)
);

CREATE TABLE artist_albums
(
    albums_id  BIGINT NOT NULL,
    artists_id BIGINT NOT NULL,
    CONSTRAINT pk_artist_albums PRIMARY KEY (albums_id, artists_id)
);

CREATE TABLE genre
(
    id         BIGINT NOT NULL,
    uuid       UUID,
    created_on TIMESTAMP WITHOUT TIME ZONE,
    version    BIGINT,
    name       VARCHAR(255),
    CONSTRAINT pk_genre PRIMARY KEY (id)
);

CREATE TABLE song
(
    id           BIGINT       NOT NULL,
    uuid         UUID,
    created_on   TIMESTAMP WITHOUT TIME ZONE,
    version      BIGINT,
    genre_id     BIGINT,
    title        VARCHAR(255) NOT NULL,
    release_date TIMESTAMP WITHOUT TIME ZONE,
    duration     BIGINT,
    preview_url  TEXT,
    file_url     TEXT,
    language     VARCHAR(255),
    album_id     BIGINT,
    CONSTRAINT pk_song PRIMARY KEY (id)
);

CREATE TABLE song_artists
(
    artists_id BIGINT NOT NULL,
    songs_id   BIGINT NOT NULL,
    CONSTRAINT pk_song_artists PRIMARY KEY (artists_id, songs_id)
);

CREATE TABLE users
(
    id                 BIGINT       NOT NULL,
    uuid               UUID,
    created_on         TIMESTAMP WITHOUT TIME ZONE,
    version            BIGINT,
    email              VARCHAR(255) NOT NULL,
    password           VARCHAR(255),
    enabled            BOOLEAN      NOT NULL,
    authorities        TEXT[],
    confirmation_token VARCHAR(255),
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

CREATE INDEX idx_song_name ON song (title);

ALTER TABLE song
    ADD CONSTRAINT FK_SONG_ON_ALBUM FOREIGN KEY (album_id) REFERENCES album (id);

ALTER TABLE song
    ADD CONSTRAINT FK_SONG_ON_GENRE FOREIGN KEY (genre_id) REFERENCES genre (id);

ALTER TABLE artist_albums
    ADD CONSTRAINT fk_artalb_on_album FOREIGN KEY (albums_id) REFERENCES album (id);

ALTER TABLE artist_albums
    ADD CONSTRAINT fk_artalb_on_artist FOREIGN KEY (artists_id) REFERENCES artist (id);

ALTER TABLE song_artists
    ADD CONSTRAINT fk_sonart_on_artist FOREIGN KEY (artists_id) REFERENCES artist (id);

ALTER TABLE song_artists
    ADD CONSTRAINT fk_sonart_on_song FOREIGN KEY (songs_id) REFERENCES song (id);