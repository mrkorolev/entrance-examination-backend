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
    @Mapping(source = "registeredAt", target = "createdAt")
    Student toEntity(StudentDto dto);

    @Mapping(source = "departmentPreferences", target = "departmentPreferenceIds")
    @Mapping(source = "examEntries", target = "examEntryIds", qualifiedByName = "toExamEntryIds")
    @Mapping(source = "createdAt", target = "registeredAt")
    StudentDto toDto(Student entity);


    @Mapping(source = "departmentPreferences", target = "departmentPreferenceIds")
    @Mapping(source = "examEntries", target = "examEntryIds", qualifiedByName = "toExamEntryIds")
    @Mapping(source = "createdAt", target = "registeredAt")
    @Mapping(target = "password", ignore = true)
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
