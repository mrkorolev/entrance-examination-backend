CREATE TABLE universities (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,

    name varchar(64) UNIQUE NOT NULL,
    description varchar(128)
);

CREATE TABLE departments (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    department_code bigint UNIQUE NOT NULL,
    university_id bigint,

    -- PROD CLAUSES
--     university_id bigint NOT NULL,
    name varchar(64) UNIQUE NOT NULL,
    preferred_grade varchar(6) NOT NULL,
    quota int NOT NULL,

    FOREIGN KEY(university_id) REFERENCES universities(id) ON DELETE CASCADE
);