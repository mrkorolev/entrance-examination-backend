CREATE TABLE students (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,

    name varchar(64) UNIQUE NOT NULL,
    email varchar(64) UNIQUE NOT NULL,
    password_encrypted varchar(128) NOT NULL,

    cgpa float NOT NULL,
    department_choices bigint[10],
    grade1_result float,
    grade2_result float,
    grade3_result float,

    created_at timestamp NOT NULL
);