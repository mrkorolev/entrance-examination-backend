package com.nksoft.entrance_examination.examination.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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
    @Null(message = "Exam entry IDs are not to be provided externally")
    private List<Long> examEntryIds;

    @NotBlank(message = "Exam center name can't be null/empty")
    @Size(max = 64, message = "Exam center name can't exceed 64 characters")
    private String name;
    @Size(max = 128, message = "Exam center address can't exceed 128 characters")
    private String address;
    @Positive(message = "Total rooms has to be a greater than zero")
    private Integer totalRooms = 20;
    @Positive(message = "Room capacity has to be a greater than zero")
    private Integer roomCapacity = 15;
}
