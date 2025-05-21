package com.nksoft.entrance_examination.mapper;

import com.nksoft.entrance_examination.dto.ExamEntryDto;
import com.nksoft.entrance_examination.entity.ExamEntry;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ExamEntryMapper {
    @Mapping(source = "examEntryId", target = "id")
    @Mapping(source = "studentId", target = "student.id")
    @Mapping(source = "examId", target = "exam.id")
    ExamEntry toEntity(ExamEntryDto dto);

    @Mapping(source = "id", target = "examEntryId")
    @Mapping(source = "student.id", target = "studentId")
    @Mapping(source = "exam.id", target = "examId")
    ExamEntryDto toDto(ExamEntry examEntry);

    List<ExamEntryDto> toDtoList(List<ExamEntry> entities);
}
