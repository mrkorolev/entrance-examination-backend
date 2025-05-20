CREATE TABLE exam_centers (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,

    name varchar(64) UNIQUE NOT NULL,
    description varchar(128),
    capacity int DEFAULT 100
);

CREATE TABLE exams (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    exam_center_id bigint,

    -- PROD CLAUSE
--     exam_center_id bigint NOT NULL,

    grade_type varchar(6) NOT NULL,
    duration_minutes int DEFAULT 60,
    date_time timestamp,
    created_at timestamp NOT NULL,

    FOREIGN KEY(exam_center_id) REFERENCES exam_centers(id) ON DELETE CASCADE
);

CREATE TABLE exam_entries (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    student_id bigint,
    exam_id bigint,
    exam_center_id bigint,

    -- PROD CLAUSES
--     student_id bigint NOT NULL,
--     exam_id bigint NOT NULL,
--     exam_center_id bigint NOT NULL,

    final_score int,
    correct_answers int,
    incorrect_answers int,
    created_at timestamp NOT NULL,

    -- single registration for each student is allowed per unique exam
    UNIQUE(student_id, exam_id),
    FOREIGN KEY(student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY(exam_id) REFERENCES exams(id) ON DELETE CASCADE,
    FOREIGN KEY(exam_center_id) REFERENCES exam_centers(id) ON DELETE CASCADE

);