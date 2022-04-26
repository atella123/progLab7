CREATE TABLE IF NOT EXISTS persons(
    id INTEGER PRIMARY KEY,
    name TEXT,
    coordinates_x REAL,
    coordinates_y BIGINT,
    creation_date DATE,
    height INTEGER,
    passpord_id TEXT,
    eye_color VARCHAR(10),
    country VARCHAR(20),
    location_x REAL,
    location_y BIGINT,
    location_name TEXT
);
CREATE SEQUENCE IF NOT EXISTS id_sequence AS INTEGER OWNED BY persons.id;