CREATE TABLE departments (
    id bigint PRIMARY KEY,
    university_id bigint,

    name varchar(64) UNIQUE NOT NULL,
    preferred_grade varchar(6) NOT NULL,
    quota int NOT NULL,

    FOREIGN KEY(university_id) REFERENCES universities(id) ON DELETE CASCADE
);