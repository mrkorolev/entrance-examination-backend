package com.nksoft.entrance_examination.examination.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ExamEntryDto {
    @Null(message = "Exam entry ID is not to be provided externally")
    private Long examEntryId;
    @NotNull(message = "Student code must be provided")
    private Long studentId;
    @NotNull(message = "Exam ID must be provided")
    private Long examCenterId;

    @Null(message = "Seat number must not be provided externally")
    private Integer seatNumber;

    @Null(message = "Registration date must not be provided externally")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;
}
