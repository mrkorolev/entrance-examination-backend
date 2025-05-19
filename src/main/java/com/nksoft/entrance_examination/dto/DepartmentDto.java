package com.nksoft.entrance_examination.dto;

import com.nksoft.entrance_examination.entity.GradeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DepartmentDto {
    @Null(message = "Department ID is not to be provided externally")
    private Long departmentId;
    @Null(message = "University ID must be provided")
    private Long universityId;
    @NotNull(message = "Preferred department grade must be provided")
    @Pattern(regexp = "TYT|AYT|YDT", message = "Exam grade type allowed values (might be changed): [TYT, AYT, YDT]")
    private GradeType preferredGrade;
    @NotBlank(message = "Department name can't be null/empty")
    private String name;
    @NotNull(message = "Department capacity must be provided")
    @Positive(message = "Department capacity must be greater than zero")
    private Integer capacity;
}
