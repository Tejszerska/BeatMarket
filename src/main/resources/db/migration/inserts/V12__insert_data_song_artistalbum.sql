INSERT INTO artist_albums (artists_id, albums_id) VALUES
                                                      (1, 1),
                                                      (2, 2),
                                                      (3, 3),
                                                      (4, 4),
                                                      (5, 5);

INSERT INTO song (album_id, genre_id, created_on, uuid, language, duration, release_date, name) VALUES
-- The Beatles (Abbey Road)
(1, 1, '2023-01-01 10:00:00+00', 'f1111111-ffff-ffff-ffff-ffffffffffff', 'ENGLISH', 259, '1969-09-26 00:00:00', 'Come Together'),
(1, 1, '2023-01-01 10:00:00+00', 'f1111112-ffff-ffff-ffff-ffffffffffff', 'ENGLISH', 182, '1969-09-26 00:00:00', 'Something'),
(1, 1, '2023-01-01 10:00:00+00', 'f1111113-ffff-ffff-ffff-ffffffffffff', 'ENGLISH', 207, '1969-09-26 00:00:00', 'Maxwell''s Silver Hammer'),

-- Queen (A Night at the Opera)
(2, 2, '2023-01-01 10:00:00+00', 'f2222221-ffff-ffff-ffff-ffffffffffff', 'ENGLISH', 354, '1975-11-21 00:00:00', 'Bohemian Rhapsody'),
(2, 2, '2023-01-01 10:00:00+00', 'f2222222-ffff-ffff-ffff-ffffffffffff', 'ENGLISH', 172, '1975-11-21 00:00:00', 'You''re My Best Friend'),
(2, 2, '2023-01-01 10:00:00+00', 'f2222223-ffff-ffff-ffff-ffffffffffff', 'ENGLISH', 219, '1975-11-21 00:00:00', 'Love of My Life'),

-- Daft Punk (Discovery)
(3, 3, '2023-01-01 10:00:00+00', 'f3333331-ffff-ffff-ffff-ffffffffffff', 'ENGLISH', 320, '2001-03-12 00:00:00', 'One More Time'),
(3, 3, '2023-01-01 10:00:00+00', 'f3333332-ffff-ffff-ffff-ffffffffffff', 'ENGLISH', 207, '2001-03-12 00:00:00', 'Aerodynamic'),
(3, 3, '2023-01-01 10:00:00+00', 'f3333333-ffff-ffff-ffff-ffffffffffff', 'ENGLISH', 301, '2001-03-12 00:00:00', 'Digital Love'),

-- Eminem (The Eminem Show)
(4, 4, '2023-01-01 10:00:00+00', 'f4444441-ffff-ffff-ffff-ffffffffffff', 'ENGLISH', 290, '2002-05-26 00:00:00', 'Without Me'),
(4, 4, '2023-01-01 10:00:00+00', 'f4444442-ffff-ffff-ffff-ffffffffffff', 'ENGLISH', 298, '2002-05-26 00:00:00', 'Cleanin'' Out My Closet'),
(4, 4, '2023-01-01 10:00:00+00', 'f4444443-ffff-ffff-ffff-ffffffffffff', 'ENGLISH', 339, '2002-05-26 00:00:00', 'Sing for the Moment'),

-- Miles Davis (Kind of Blue)
(5, 5, '2023-01-01 10:00:00+00', 'f5555551-ffff-ffff-ffff-ffffffffffff', 'INSTRUMENTAL', 562, '1959-08-17 00:00:00', 'So What'),
(5, 5, '2023-01-01 10:00:00+00', 'f5555552-ffff-ffff-ffff-ffffffffffff', 'INSTRUMENTAL', 574, '1959-08-17 00:00:00', 'Freddie Freeloader'),
(5, 5, '2023-01-01 10:00:00+00', 'f5555553-ffff-ffff-ffff-ffffffffffff', 'INSTRUMENTAL', 337, '1959-08-17 00:00:00', 'Blue in Green');