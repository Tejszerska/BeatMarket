CREATE SEQUENCE IF NOT EXISTS license_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE license
(
    id              BIGINT NOT NULL,
    uuid            UUID,
    created_on      TIMESTAMP WITHOUT TIME ZONE,
    version         BIGINT,
    certificate_key VARCHAR(255),
    user_id         BIGINT,
    payment_id      BIGINT,
    song_id         BIGINT,
    tier            VARCHAR(255),
    valid_from      TIMESTAMP WITHOUT TIME ZONE,
    valid_to        TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_license PRIMARY KEY (id)
);

CREATE TABLE song_price
(
    song_id    BIGINT       NOT NULL,
    uuid       UUID,
    created_on TIMESTAMP WITHOUT TIME ZONE,
    version    BIGINT,
    tier       VARCHAR(255) NOT NULL,
    price      DECIMAL,
    currency   VARCHAR(255),
    CONSTRAINT pk_songprice PRIMARY KEY (song_id, tier)
);