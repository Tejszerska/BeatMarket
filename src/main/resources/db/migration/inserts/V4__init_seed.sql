-- Seed users
INSERT INTO users (id, uuid, created_on, version, email, password, enabled, authorities) VALUES
                                                                                             (1, gen_random_uuid(), now(), 0, 'a@dmin', '$2a$10$3HDR4jD.jsKtNRoYd.j8G.3hJd9zT.4zP5htmlKmcxnUC6pTz94uW', true, '{ROLE_ADMIN,ROLE_CUSTOMER}'),
                                                                                             (2, gen_random_uuid(), now(), 0, 'u@ser2', '$2a$10$p3SEF7EXTQYh..Y2f12FF.Y7wHR5Ct4zmlypld2Z8Ola/hFQYWgiO', true, '{ROLE_CUSTOMER}'),
                                                                                             (3, gen_random_uuid(), now(), 0, 'u@ser3', '$2a$10$p3SEF7EXTQYh..Y2f12FF.Y7wHR5Ct4zmlypld2Z8Ola/hFQYWgiO', true, '{ROLE_CUSTOMER}');

-- Update users sequence
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));

-- Seed genres
INSERT INTO genre (id, uuid, created_on, version, name) VALUES
                                                            (1, gen_random_uuid(), now(), 0, 'Neural Synth'),
                                                            (2, gen_random_uuid(), now(), 0, 'Voidwave');

SELECT setval('genre_id_seq', (SELECT MAX(id) FROM genre));

-- Seed artists
INSERT INTO artist (id, uuid, created_on, version, name, image_url) VALUES
                                                                        (1, gen_random_uuid(), now(), 0, 'Aethelion Cortex', 'https://s3.amazonaws.com/beatmarket/artists/aethelion.jpg'),
                                                                        (2, gen_random_uuid(), now(), 0, 'Neon Drift Entity', 'https://s3.amazonaws.com/beatmarket/artists/neondrift.jpg');

SELECT setval('artist_id_seq', (SELECT MAX(id) FROM artist));

-- Seed albums
INSERT INTO album (id, uuid, created_on, version, title, release_date, cover_url) VALUES
                                                                                      (1, gen_random_uuid(), now(), 0, 'Fragments of Tomorrow', '2026-07-25', 'https://s3.amazonaws.com/beatmarket/albums/fragments.jpg'),
                                                                                      (2, gen_random_uuid(), now(), 0, 'Silicon Echos', '2026-07-28', 'https://s3.amazonaws.com/beatmarket/albums/silicon.jpg');

SELECT setval('album_id_seq', (SELECT MAX(id) FROM album));

-- Link artists with albums
INSERT INTO artist_albums (albums_id, artists_id) VALUES
                                                      (1, 1),
                                                      (2, 2);

-- Seed songs
INSERT INTO song (id, uuid, created_on, version, genre_id, title, release_date, duration, preview_url, file_url, language, album_id) VALUES
                                                                                                                                         (1, gen_random_uuid(), now(), 0, 1, 'Quantum Lullaby', '2026-07-25', 185, 'https://s3.aws.../preview1.mp3', 'https://s3.aws.../full1.wav', 'EN', 1),
                                                                                                                                         (2, gen_random_uuid(), now(), 0, 1, 'Cybernetic Dawn', '2026-07-25', 210, 'https://s3.aws.../preview2.mp3', 'https://s3.aws.../full2.wav', 'EN', 1),
                                                                                                                                         (3, gen_random_uuid(), now(), 0, 2, 'Abyssal Overdrive', '2026-07-28', 198, 'https://s3.aws.../preview3.mp3', 'https://s3.aws.../full3.wav', 'EN', 2);

SELECT setval('song_id_seq', (SELECT MAX(id) FROM song));

-- Link songs with artists
INSERT INTO song_artists (artists_id, songs_id, artist_order) VALUES
                                                                  (1, 1, 0),
                                                                  (2, 1, 1),
                                                                  (1, 2, 0),
                                                                  (2, 3, 0);

-- Seed song prices
INSERT INTO song_price (song_id, uuid, created_on, version, tier, price, currency) VALUES
                                                                                       (1, gen_random_uuid(), now(), 0, 'STANDARD', 19.99, 'USD'),
                                                                                       (1, gen_random_uuid(), now(), 0, 'COMMERCIAL', 79.99, 'USD'),
                                                                                       (2, gen_random_uuid(), now(), 0, 'STANDARD', 24.99, 'USD'),
                                                                                       (3, gen_random_uuid(), now(), 0, 'STANDARD', 15.99, 'USD'),
                                                                                       (3, gen_random_uuid(), now(), 0, 'UNLIMITED', 199.99, 'USD');