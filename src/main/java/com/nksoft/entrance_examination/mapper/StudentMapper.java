package com.nksoft.entrance_examination.mapper;

import com.nksoft.entrance_examination.dto.StudentDto;
import com.nksoft.entrance_examination.entity.ExamEntry;
import com.nksoft.entrance_examination.entity.Student;
import jdk.jfr.Name;
import org.mapstruct.IterableMapping;
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

    @Mapping(source = "examEntries", target = "examEntryIds", qualifiedByName = "toExamEntryIds")
    @Mapping(source = "passwordHash", target = "password")
    @Mapping(source = "createdAt", target = "registeredAt")
    StudentDto toDto(Student entity);

    @Mapping(source = "examEntries", target = "examEntryIds", qualifiedByName = "toExamEntryIds")
    @Mapping(source = "passwordHash", target = "password", ignore = true)
    @Mapping(source = "createdAt", target = "registeredAt")
    @Named("sanitized")
    StudentDto toDtoSanitized(Student entity);

    List<StudentDto> toDtoList(List<Student> entities);

    @Named("toExamEntryIds")
    default List<Long> toExamEntryIds(List<ExamEntry> examEntries) {
        return examEntries.stream()
                .map(ExamEntry::getId)
                .toList();
    }
}
