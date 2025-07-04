CREATE TABLE exams (
    grade_type varchar(6) PRIMARY KEY CHECK (exam_type IN ('GRADE1', 'GRADE2', 'GRADE3')),

    duration_minutes int NOT NULL,
    exam_start_time timestamp NOT NULL,
    created_at timestamp NOT NULL,

    booklet_a_keys text,
    booklet_b_keys text,
    booklet_c_keys text,

    mean float,
    standard_deviation float,

    CHECK (
        length(booklet_a_keys) = length(booklet_b_keys) AND
        length(booklet_b_keys) = length(booklet_c_keys)
    )
);

CREATE TABLE exam_results (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    exam_entry_id bigint NOT NULL,
    grade_type varchar(6) NOT NULL CHECK (grade_type IN ('GRADE1', 'GRADE2', 'GRADE3')),

    booklet_type char(1) NOT NULL CHECK (booklet_type IN ('A', 'B', 'C')),
    raw_answers text NOT NULL,

    correct int NOT NULL,
    incorrect int NOT NULL,
    unanswered int NOT NULL,

    net_score float NOT NULL,
    normalized_score float NOT NULL,
    created_at timestamp NOT NULL,

    UNIQUE(exam_entry_id, grade_type),
    FOREIGN KEY (grade_type) REFERENCES exams(grade_type) ON DELETE CASCADE,
    FOREIGN KEY (exam_entry_id) REFERENCES exam_entries(id) ON DELETE CASCADE
);