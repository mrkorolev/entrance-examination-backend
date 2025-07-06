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