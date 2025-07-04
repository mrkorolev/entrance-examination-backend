package com.nksoft.entrance_examination.examination.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ExamEntryDto {
    @Null(message = "Exam entry ID is not to be provided externally")
    private Long examEntryId;
    @NotNull(message = "Student code must be provided")
    private Long studentCode;
    @NotNull(message = "Exam ID must be provided")
    private Long examId;

    @Null(message = "Final score is not to be provided externally")
    private Integer finalScore;
    @Null(message = "Correct answers is not to be provided externally")
    private Integer correctAnswers;
    @Null(message = "Incorrect answers is not to be provided externally")
    private Integer incorrectAnswers;

    @Null(message = "Registration date is not to be provided externally")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;

    @Null(message= "Results dater is not to be provided externally")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime resultsReceivedAt;
}
