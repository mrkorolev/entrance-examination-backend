package com.nksoft.entrance_examination.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@NoArgsConstructor
public class StudentDto {
    @Null(message = "Student ID is not to be provided externally")
    private Long studentId;
    @Null(message = "Exam entry IDs are not to be provided externally")
    private int[] examEntryIds;
    @NotBlank(message = "First name can't be null/empty")
    private String firstName;
    @NotBlank(message = "Last name can't be null/empty")
    private String lastName;
    @NotBlank(message = "Email can't be null/empty")
    private String email;
    @NotBlank(message = "Password can't be null/empty")
    private String password;
    @Positive(message = "CGPA is supposed to be in range: 0 < cgpa <= 100")
    @Max(value = 100, message = "CGPA maximum value is 100")
    private Float cgpa;
    @Null(message = "Registration date is not to be provided externally")
    private LocalDateTime registeredAt;
}
