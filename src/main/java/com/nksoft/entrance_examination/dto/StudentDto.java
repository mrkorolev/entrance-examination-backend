package com.nksoft.entrance_examination.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nksoft.entrance_examination.entity.StudentStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
public class StudentDto {
    @NotNull(message = "Student code must be provided")
    private Long studentCode;
    @Null(message = "Student status is not to be provided externally")
    private StudentStatus status;

    @Null(message = "Exam entry IDs are not to be provided externally")
    private List<Long> examEntryIds;

    @NotBlank(message = "Name can't be null/empty")
    private String name;
    @NotBlank(message = "Email can't be null/empty")
    private String email;
    @NotBlank(message = "Password can't be null/empty")
    private String password;

    @Null(message = "Student's department choices are not to be provided externally")
    private Long[] preferredDepartmentIds;
    @Null(message = "Student's final department placement is not to be provided externally")
    private Integer placedPreferenceIdx;

    @Min(value = 0, message = "CGPA minimum value is 0")
    @Max(value = 100, message = "CGPA maximum value is 100")
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
