ALTER TABLE album_artist DROP CONSTRAINT pk_artist_albums;
ALTER TABLE song_artist DROP CONSTRAINT pk_song_artists;


ALTER TABLE album_artist
    ADD CONSTRAINT pk_album_artist PRIMARY KEY (album_id, artist_order);

ALTER TABLE song_artist
    ADD CONSTRAINT pk_song_artist PRIMARY KEY (artist_order, song_id);


ALTER TABLE album_artist
    ADD CONSTRAINT uk_album_artist UNIQUE (album_id, artist_id);

ALTER TABLE song_artist
    ADD CONSTRAINT uk_song_artist UNIQUE (song_id, artist_id);