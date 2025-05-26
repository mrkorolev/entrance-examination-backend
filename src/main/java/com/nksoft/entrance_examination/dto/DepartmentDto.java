package com.nksoft.entrance_examination.dto;

import com.nksoft.entrance_examination.entity.GradeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DepartmentDto {
    @NotNull(message = "Department code must be provided")
    private Long departmentCode;
    @NotNull(message = "University ID must be provided")
    private Long universityId;

    @NotNull(message = "Preferred department grade must be provided")
    @Pattern(regexp = "GRADE1|GRADE2|GRADE3",
            message = "Exam grade type allowed values (might be changed): [GRADE1, GRADE2, GRADE3]")
    private GradeType preferredGrade;
    @NotBlank(message = "Department name can't be null/empty")
    private String name;
    @Positive(message = "Department capacity must be greater than zero")
    private Integer quota;
}
