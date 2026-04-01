ALTER TABLE song
    ADD uuid UUID DEFAULT uuid_generate_v4() NOT NULL UNIQUE ;