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