package com.nksoft.entrance_examination.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nksoft.entrance_examination.entity.GradeType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ExamDto {
    @Null(message = "Exam ID is not to be provided externally")
    private Long examId;
    @NotNull(message = "Exam center ID must be provided")
    private Long examCenterId;

    @NotNull(message = "Exam grade type must be provided")
    @Pattern(regexp = "GRADE1|GRADE2|GRADE3", message = "Exam grade type allowed values (might be changed): [GRADE1, GRADE2, GRADE3]")
    private GradeType examGradeType;
    @Positive(message = "Exam duration in minutest is to be greater than zero")
    @NotNull(message = "Exam duration in minutes must be provided")
    private Integer durationInMinutes;

    @Future(message = "Exam date must be in the future")
    @NotNull(message = "Exam date and time must be provided")
    private LocalDateTime examDateTime;
    @Null(message = "Exam creation date is not to be provided externally")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
