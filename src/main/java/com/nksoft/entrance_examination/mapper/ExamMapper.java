package com.nksoft.entrance_examination.mapper;

import com.nksoft.entrance_examination.dto.ExamDto;
import com.nksoft.entrance_examination.entity.Exam;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ExamMapper {
    @Mapping(source = "examId", target = "id")
    @Mapping(source = "examGradeType", target = "targetGrade")
    @Mapping(source = "examDateTime", target = "dateAndTime")
    Exam toEntity(ExamDto dto);

    @Mapping(source = "id", target = "examId")
    @Mapping(source = "targetGrade", target = "examGradeType")
    @Mapping(source = "dateAndTime", target = "examDateTime")
    ExamDto toDto(Exam entity);

    List<ExamDto> toDtoList(List<Exam> entities);
}
