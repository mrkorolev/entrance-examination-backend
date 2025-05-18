CREATE TABLE students (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    first_name varchar(30) NOT NULL,
    last_name varchar(30) NOT NULL,
    cgpa float DEFAULT 0,
    email varchar(64) UNIQUE NOT NULL,
    password_encrypted varchar(128) NOT NULL,
    created_at timestamptz
);