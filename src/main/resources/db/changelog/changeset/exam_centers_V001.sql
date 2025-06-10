CREATE TABLE exam_centers (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,

    name varchar(64) UNIQUE NOT NULL,
    address varchar(128),
    total_rooms int NOT NULL,
    room_capacity int NOT NULL
);