package com.nksoft.entrance_examination.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nksoft.entrance_examination.entity.GradeType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@NoArgsConstructor
public class ExamDto {
    @Null(message = "Exam ID is not to be provided externally")
    private Long examId;
    @NotNull(message = "Exam center ID must be provided")
    private Long examCenterId;
    @NotNull(message = "Exam grade type must be provided")
    @Pattern(regexp = "TYT|AYT|YDT", message = "Exam grade type allowed values (might be changed): [TYT, AYT, YDT]")
    private GradeType examGradeType;
    @Future(message = "Exam date must be in the future")
    private LocalDateTime examDate;
    @Null(message = "Exam creation date is not to be provided externally")
    private LocalDateTime createdAt;
    @Positive(message = "Total exam questions is to be greater than zero")
    private Integer totalQuestions;
    @Positive(message = "Exam duration in minutest is to be greater than zero")
    private Integer durationInMinutes;
}
