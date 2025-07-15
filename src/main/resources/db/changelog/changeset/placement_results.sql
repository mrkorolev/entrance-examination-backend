CREATE TABLE placement_results (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    department_id bigint NOT NULL,
    student_id bigint UNIQUE NOT NULL,

    final_score float NOT NULL,
    preferred_by_department varchar(6) NOT NULL,

    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);