CREATE TABLE students (
    student_code bigint PRIMARY KEY,

    name varchar(64) NOT NULL,
    email varchar(64) UNIQUE NOT NULL,
    password_hash varchar(255) NOT NULL,
    status varchar(20) NOT NULL,

    preferred_dep_ids bigint[],
    placed_pref_idx int,
    cgpa float NOT NULL,
    grade1_result float,
    grade2_result float,
    grade3_result float,

    created_at timestamp NOT NULL
);