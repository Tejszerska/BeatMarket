ALTER TABLE song RENAME COLUMN file_url TO track_file_key;
ALTER TABLE song RENAME COLUMN preview_url TO preview_file_key;
ALTER TABLE album RENAME COLUMN cover_url TO cover_file_key;
ALTER TABLE artist RENAME COLUMN image_url TO image_file_key;