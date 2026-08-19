CREATE SEQUENCE IF NOT EXISTS payment_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE payment
(
    id                BIGINT NOT NULL,
    uuid              UUID,
    created_on        TIMESTAMP WITHOUT TIME ZONE,
    edited_on    TIMESTAMP WITHOUT TIME ZONE,
    active       BOOLEAN NOT NULL,
    version           BIGINT,
    user_id           BIGINT,
    license_id        BIGINT,
    amount            DECIMAL,
    currency          VARCHAR(255),
    status            VARCHAR(255),
    stripe_session_id VARCHAR(255),
    CONSTRAINT pk_payment PRIMARY KEY (id)
);