package com.nksoft.entrance_examination.mapper;

import com.nksoft.entrance_examination.dto.UniversityDto;
import com.nksoft.entrance_examination.entity.Department;
import com.nksoft.entrance_examination.entity.University;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UniversityMapper {
    @Mapping(source = "universityId", target = "id")
    University toEntity(UniversityDto dto);

    @Mapping(source = "id", target = "universityId")
    @Mapping(source = "departments", target = "departmentIds", qualifiedByName = "toDepartmentIds")
    UniversityDto toDto(University entity);

    List<UniversityDto> toDtoList(List<University> entities);

    @Named("toDepartmentIds")
    default List<Long> toDepartmentIds(List<Department> departments) {
        return departments.stream()
                .map(Department::getId)
                .toList();
    }
}
