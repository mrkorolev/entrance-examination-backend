package com.nksoft.entrance_examination.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ExamCenterDto {
    @Null(message = "Exam center ID is not to be provided externally")
    private Long examCenterId;
    @NotBlank(message = "Exam center name can't be null/empty")
    private String name;
    private String address;
    @Positive(message = "Exam center capacity should be greater than zero")
    private Integer capacity;
}
