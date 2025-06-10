CREATE TABLE students (
    student_code bigint PRIMARY KEY,

    status varchar(20) NOT NULL,
    name varchar(64) NOT NULL,
    email varchar(64) UNIQUE NOT NULL,
    password_hash varchar(255) NOT NULL,

    preferred_dep_ids bigint[],
    placed_pref_idx int,

    cgpa float NOT NULL,
    grade1_result float,
    grade2_result float,
    grade3_result float,

    created_at timestamp NOT NULL
);

CREATE TABLE exam_entries (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    student_code bigint,
    exam_center_id bigint,

    -- PROD CLAUSES
--     student_code bigint NOT NULL,
--     exam_center_id bigint NOT NULL,

    seat_number int NOT NULL,
    created_at timestamp NOT NULL,

    UNIQUE(student_code, exam_center_id),
    UNIQUE(exam_center_id, seat_number),
    FOREIGN KEY(student_code) REFERENCES students(student_code) ON DELETE CASCADE,
    FOREIGN KEY(exam_center_id) REFERENCES exam_centers(id) ON DELETE CASCADE
);