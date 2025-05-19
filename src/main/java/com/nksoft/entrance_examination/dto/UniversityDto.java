package com.nksoft.entrance_examination.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UniversityDto {
    @Null(message = "University ID is not to be provided externally")
    private Long id;
    @NotBlank(message = "University name can't be null/empty")
    private String name;
    private String description;
    @Null(message = "Department IDs are not to be provided externally")
    private int[] departmentIds;
}
