CREATE TABLE universities (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name varchar(64) UNIQUE NOT NULL,
    address varchar(128)
);

CREATE TABLE departments (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name varchar(64) UNIQUE NOT NULL,
    university_id bigint,
    preferred_grade varchar(3) NOT NULL,
    capacity int DEFAULT 20,

    FOREIGN KEY(university_id) REFERENCES universities(id) ON DELETE CASCADE
);