package com.nksoft.entrance_examination.student.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nksoft.entrance_examination.common.validator.annotations.DecimalPrecision;
import com.nksoft.entrance_examination.student.model.StudentStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class StudentDto {
    @NotNull(message = "Student code must be provided")
    private Long studentCode;

    @Null(message = "Exam entry ID are not to be provided externally")
    private Long examEntryId;

    @NotBlank(message = "Name can't be null/empty")
    @Size(max = 64, message = "Name must not exceed 64 characters")
    private String name;
    @NotBlank(message = "Email can't be null/empty")
    @Size(max = 64, message = "Email must not exceed 64 characters")
    @Email
    private String email;
    @NotBlank(message = "Password can't be null/empty")
    @Size(min = 8, max = 16, message = "Password should be from 8 to 16 characters long")
    private String password;
    @Null(message = "Student status is not to be provided externally")
    private StudentStatus status;

    @Null(message = "Student's department choices are not to be provided externally")
    private Long[] preferredDepartmentIds;
    @Null(message = "Student's final department placement is not to be provided externally")
    private Integer placedPreferenceIdx;

    @Min(value = 0, message = "CGPA minimum value is 0")
    @Max(value = 100, message = "CGPA maximum value is 100")
    @DecimalPrecision(maxDecimalPlaces = 2, message = "CGPA should be up to 2 decimal places")
    private Float cgpa;
    @Null(message = "Student's exam results for grade1 are not to be provided externally")
    private Float grade1Result;
    @Null(message = "Student's exam results for grade2 are not to be provided externally")
    private Float grade2Result;
    @Null(message = "Student's exam results for grade3 are not to be provided externally")
    private Float grade3Result;

    @Null(message = "Registration date is not to be provided externally")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime registeredAt;
}
