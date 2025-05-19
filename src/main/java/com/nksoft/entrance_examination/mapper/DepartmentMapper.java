package com.nksoft.entrance_examination.mapper;

import com.nksoft.entrance_examination.dto.DepartmentDto;
import com.nksoft.entrance_examination.entity.Department;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DepartmentMapper {
    @Mapping(source = "departmentId", target = "id")
    @Mapping(source = "universityId", target = "university.id")
    DepartmentDto toDto(Department entity);

    @Mapping(source = "id", target = "departmentId")
    @Mapping(source = "university.id", target = "universityId")
    Department toEntity(DepartmentDto dto);

    List<DepartmentDto> toDtoList(List<Department> entityList);
}
