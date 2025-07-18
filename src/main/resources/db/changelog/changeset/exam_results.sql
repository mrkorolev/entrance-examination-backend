CREATE TABLE exam_results (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    exam_entry_id bigint,
--     exam_entry_id bigint NOT NULL,
    exam_id int NOT NULL,

    booklet_type char(1) NOT NULL,
    raw_answers text NOT NULL,
    correct int NOT NULL,
    incorrect int NOT NULL,
    unanswered int NOT NULL,
    raw_score float,
    normalized_score float,
    created_at timestamp NOT NULL,

    UNIQUE(exam_entry_id, exam_id),
    FOREIGN KEY (exam_id) REFERENCES exams(id) ON DELETE CASCADE,
    FOREIGN KEY (exam_entry_id) REFERENCES exam_entries(id) ON DELETE CASCADE
);