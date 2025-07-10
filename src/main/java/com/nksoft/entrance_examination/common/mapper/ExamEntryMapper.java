package com.nksoft.entrance_examination.common.mapper;

import com.nksoft.entrance_examination.examination.dto.ExamEntryDto;
import com.nksoft.entrance_examination.examination.model.ExamEntry;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ExamEntryMapper {
    @Mapping(source = "examEntryId", target = "id")
    @Mapping(source = "studentId", target = "student.id")
    @Mapping(source = "examCenterId", target = "examCenter.id")
    ExamEntry toEntity(ExamEntryDto dto);

    @Mapping(source = "id", target = "examEntryId")
    @Mapping(source = "student.id", target = "studentId")
    @Mapping(source = "examCenter.id", target = "examCenterId")
    ExamEntryDto toDto(ExamEntry examEntry);

    List<ExamEntryDto> toDtoList(List<ExamEntry> entities);
}
