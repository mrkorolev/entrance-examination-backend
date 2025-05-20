package com.nksoft.entrance_examination.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ExamCenterDto {
    @Null(message = "Exam center ID is not to be provided externally")
    private Long examCenterId;
    @Null(message = "Exam IDs are not to be provided externally")
    private List<Long> examIds;
    @NotBlank(message = "Exam center name can't be null/empty")
    private String name;
    private String address;
    @Positive(message = "Exam center capacity should be greater than zero")
    private Integer capacity;
}
