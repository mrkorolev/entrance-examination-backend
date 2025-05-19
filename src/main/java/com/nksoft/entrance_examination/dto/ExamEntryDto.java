package com.nksoft.entrance_examination.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ExamEntryDto {
    @Null(message = "Exam entry ID is not to be provided externally")
    private Long examEntryId;
    @NotNull(message = "Student ID must be provided")
    private Long studentId;
    @NotNull(message = "Exam ID must be provided")
    private Long examId;
    @Null(message = "Total score is not to be provided externally")
    private Integer totalScore;
    @Null(message = "Correct answers is not to be provided externally")
    private Integer correctAnswers;
    @Null(message = "Incorrect answers is not to be provided externally")
    private Integer incorrectAnswers;
}
