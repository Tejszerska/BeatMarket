CREATE TABLE users
(
    id          BIGSERIAL       NOT NULL,
    uuid        UUID,
    created_on  TIMESTAMP WITHOUT TIME ZONE,
    version     BIGINT,
    email       VARCHAR(255) NOT NULL UNIQUE,
    password    VARCHAR(255),
    enabled     BOOLEAN      NOT NULL,
    authorities TEXT[],
    CONSTRAINT pk_users PRIMARY KEY (id)
);
