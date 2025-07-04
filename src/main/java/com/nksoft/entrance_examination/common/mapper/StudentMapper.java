package com.nksoft.entrance_examination.common.mapper;

import com.nksoft.entrance_examination.student.dto.StudentDto;
import com.nksoft.entrance_examination.student.model.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StudentMapper {
    @Mapping(source = "password", target = "passwordHash")
    @Mapping(source = "registeredAt", target = "createdAt")
    Student toEntity(StudentDto dto);

//    @Mapping(source = "examEntry.id", target = "examEntryId")
    @Mapping(source = "passwordHash", target = "password")
    @Mapping(source = "createdAt", target = "registeredAt")
    StudentDto toDto(Student entity);

//    @Mapping(source = "examEntry.id", target = "examEntryId")
    @Mapping(source = "passwordHash", target = "password", ignore = true)
    @Mapping(source = "createdAt", target = "registeredAt")
    @Named("sanitized")
    StudentDto toDtoSanitized(Student entity);

    List<StudentDto> toDtoList(List<Student> entities);
}
