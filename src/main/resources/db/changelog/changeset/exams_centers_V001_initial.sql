CREATE TABLE exam_centers (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name varchar(64) UNIQUE NOT NULL,
    address varchar(128),
    capacity int DEFAULT 100
);

CREATE TABLE exams (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    exam_center_id bigint,
    exam_type varchar(3) NOT NULL,
    total_questions int NOT NULL,
    duration_minutes int DEFAULT 60,
    exam_date timestamptz,
    created_at timestamptz,

    FOREIGN KEY(exam_center_id) REFERENCES exam_centers(id) ON DELETE CASCADE
);

CREATE TABLE exam_entries (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    student_id bigint,
    exam_id bigint,
    total_score int,
    correct_answers int,
    incorrect_answers int,

    -- single registration for each student is allowed per unique exam
    UNIQUE(student_id, exam_id),
    FOREIGN KEY(student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY(exam_id) REFERENCES exams(id) ON DELETE CASCADE
);