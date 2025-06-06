CREATE TABLE students (
    student_code bigint PRIMARY KEY,

    status varchar(20) NOT NULL,
    name varchar(64) NOT NULL,
    email varchar(64) UNIQUE NOT NULL,
    password_hash varchar(255) NOT NULL,

    preferred_dep_ids bigint[],
    placed_dep_idx int,

    cgpa float NOT NULL,
    grade1_result float,
    grade2_result float,
    grade3_result float,

    created_at timestamp NOT NULL
);

CREATE TABLE exam_entries (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    student_code bigint,
    exam_id bigint,

    -- PROD CLAUSES
--     student_id bigint NOT NULL,
--     exam_id bigint NOT NULL,

    final_score int,
    correct_answers int,
    incorrect_answers int,
    created_at timestamp NOT NULL,
    results_received_at timestamp,

    -- single registration for each student is allowed per unique exam
    UNIQUE(student_code, exam_id),
    FOREIGN KEY(student_code) REFERENCES students(student_code) ON DELETE CASCADE,
    FOREIGN KEY(exam_id) REFERENCES exams(id) ON DELETE CASCADE
);