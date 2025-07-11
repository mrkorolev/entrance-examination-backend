CREATE TABLE exams (
    id int PRIMARY KEY GENERATED ALWAYS AS IDENTITY,

    grade_type varchar(6) NOT NULL,
    duration_minutes int NOT NULL,
    exam_date_time timestamp NOT NULL,
    a_booklet_keys text,
    b_booklet_keys text,
    c_booklet_keys text,

    mean float,
    standard_deviation float,
    created_at timestamp NOT NULL,

    CHECK (
        length(a_booklet_keys) = length(b_booklet_keys) AND
        length(b_booklet_keys) = length(c_booklet_keys)
    )
);