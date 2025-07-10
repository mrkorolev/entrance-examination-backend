package com.nksoft.entrance_examination.department.dto;

import com.nksoft.entrance_examination.common.validator.annotations.ValidEnum;
import com.nksoft.entrance_examination.examination.model.GradeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DepartmentDto {
    @NotNull(message = "Department code must be provided")
    private Long departmentId;
    @NotNull(message = "University ID must be provided")
    private Long universityId;

    @NotBlank(message = "Department name can't be null/empty")
    private String name;
    @NotNull(message = "Preferred department grade must be provided")
    @ValidEnum(enumClass = GradeType.class)
    private GradeType preferredGrade;
    @NotNull(message = "Department quota must be provided")
    @Positive(message = "Department quota must be greater than zero")
    private Integer quota;
}
