package com.nksoft.entrance_examination.examination.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nksoft.entrance_examination.examination.model.BookletType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ExamResultDto {
    private Long resultId;
    private Long studentCode;
    private Long examEntryId;
    private Long examId;
    private BookletType bookletType;
    private String rawAnswers;
    private int correctCount;
    private int incorrectCount;
    private int unansweredCount;
    private Float netScore;
    private Float normalizedScore;
    private Float finalScore;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdDate;
}
