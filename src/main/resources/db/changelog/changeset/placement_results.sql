CREATE TABLE placement_results (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    department_code bigint UNIQUE NOT NULL,
    student_code bigint UNIQUE NOT NULL,

    placement_rank int NOT NULL,
    created_at TIMESTAMP NOT NULL,

    FOREIGN KEY (student_code) REFERENCES students(student_code) ON DELETE CASCADE,
    FOREIGN KEY (department_code) REFERENCES departments(department_code) ON DELETE CASCADE
);