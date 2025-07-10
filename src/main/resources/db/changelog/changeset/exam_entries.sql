CREATE TABLE exam_entries (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    student_id bigint  UNIQUE NOT NULL,
    exam_center_id bigint NOT NULL,

    seat_number int NOT NULL,
    created_at timestamp NOT NULL,

    UNIQUE(student_id, exam_center_id),
    UNIQUE(exam_center_id, seat_number),
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (exam_center_id) REFERENCES exam_centers(id) ON DELETE CASCADE
);