package com.nksoft.entrance_examination.department.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class UniversityDto {
    @Null(message = "University ID is not to be provided externally")
    private Long universityId;
    @Null(message = "Department IDs are not to be provided externally")
    private List<Long> departmentIds;
    @NotBlank(message = "University name can't be null/empty")
    @Size(max = 64, message = "Name must not exceed 64 characters")
    private String name;
    @Size(max = 128, message = "Description must not exceed 128 characters")
    private String description;
}
