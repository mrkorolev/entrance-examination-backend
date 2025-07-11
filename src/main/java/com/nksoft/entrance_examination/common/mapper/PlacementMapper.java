package com.nksoft.entrance_examination.common.mapper;

import com.nksoft.entrance_examination.placement.PlacementResult;
import com.nksoft.entrance_examination.placement.PlacementResultDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PlacementMapper {
    @Mapping(source = "id", target = "placementId")
    @Mapping(source = "department.id", target = "departmentId")
    @Mapping(source = "student.id", target = "studentId")
    @Mapping(source= "grade", target = "gradeType")
    PlacementResultDto toDto(PlacementResult examEntry);

    List<PlacementResultDto> toDtoList(List<PlacementResult> entities);
}
