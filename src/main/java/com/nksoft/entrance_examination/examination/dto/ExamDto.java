package com.nksoft.entrance_examination.examination.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nksoft.entrance_examination.common.validator.annotations.ValidEnum;
import com.nksoft.entrance_examination.examination.model.GradeType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ExamDto {
    @Null(message = "Exam ID must not be provided externally")
    private Long examId;
    @Null(message = "Exam result IDs must not be provided externally")
    private List<Long> examResultIds;

    @NotNull(message = "Exam grade type must be provided")
    @ValidEnum(enumClass = GradeType.class)
    private GradeType examGradeType;

    @Future(message = "Exam date-time must be in the future")
    @NotNull(message = "Exam date-time must be provided")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime examStartTime;

    @Positive(message = "Exam duration in minutest is to be greater than zero")
    @NotNull(message = "Exam duration in minutes must be provided")
    private Integer durationInMinutes;

    @Null(message = "Answer keys for any booklet must not be provided externally")
    private String bookletAKeys;
    @Null(message = "Answer keys for any booklet must not be provided externally")
    private String bookletBKeys;
    @Null(message = "Answer keys for any booklet must not be provided externally")
    private String bookletCKeys;

    @Null(message = "Mean is not to be provided externally")
    private Float mean;
    @Null(message = "Standard Deviation must not be provided externally")
    private Float standardDeviation;

    @Null(message = "Exam creation date must not be provided externally")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;
}
