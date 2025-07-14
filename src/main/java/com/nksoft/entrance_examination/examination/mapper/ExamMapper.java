package com.nksoft.entrance_examination.examination.mapper;

import com.nksoft.entrance_examination.examination.dto.ExamDto;
import com.nksoft.entrance_examination.examination.model.Exam;
import com.nksoft.entrance_examination.examination.model.ExamResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ExamMapper {
    @Mapping(source = "examGradeType", target = "gradeType")
    @Mapping(source = "examStartTime", target = "startTime")
    Exam toEntity(ExamDto dto);

    @Mapping(source = "id", target = "examId")
    @Mapping(source = "results", target = "examResultIds", qualifiedByName = "toExamResultIds")
    @Mapping(source = "gradeType", target = "examGradeType")
    @Mapping(source = "startTime", target = "examStartTime")
    ExamDto toDto(Exam entity);

    List<ExamDto> toDtoList(List<Exam> entities);

    @Named("toExamResultIds")
    default List<Long> toExamResultIds(List<ExamResult> results) {
        return results.stream()
                .map(ExamResult::getId)
                .toList();
    }
}
