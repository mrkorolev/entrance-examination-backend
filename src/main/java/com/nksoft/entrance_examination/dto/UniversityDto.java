package com.nksoft.entrance_examination.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
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
    private String name;
    private String description;
}
