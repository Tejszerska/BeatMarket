CREATE TABLE song
(
    id           BIGINT       NOT NULL,
    artist       VARCHAR(255) NOT NULL,
    name         VARCHAR(255) NOT NULL,
    release_date TIMESTAMP WITHOUT TIME ZONE,
    duration     BIGINT,
    language     VARCHAR(255),
    CONSTRAINT pk_song PRIMARY KEY (id)
);