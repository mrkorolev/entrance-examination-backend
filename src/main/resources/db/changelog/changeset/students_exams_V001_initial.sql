CREATE TABLE students (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,

    student_code bigint UNIQUE NOT NULL,
    name varchar(64) NOT NULL,
    email varchar(64) UNIQUE NOT NULL,
    password_encrypted varchar(128) NOT NULL,

    department_preferences bigint[10],
    placed_preference_idx int,

    cgpa float NOT NULL,
    grade1_result float,
    grade2_result float,
    grade3_result float,

    created_at timestamp NOT NULL
);

CREATE TABLE exam_entries (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    student_id bigint,
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
    UNIQUE(student_id, exam_id),
    FOREIGN KEY(student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY(exam_id) REFERENCES exams(id) ON DELETE CASCADE
);