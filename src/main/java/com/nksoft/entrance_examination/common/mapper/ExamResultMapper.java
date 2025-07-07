package com.nksoft.entrance_examination.common.mapper;

import com.nksoft.entrance_examination.examination.dto.ExamResultDto;
import com.nksoft.entrance_examination.examination.model.ExamResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ExamResultMapper {
    @Mapping(source = "id", target = "resultId")
    @Mapping(source = "examEntry.student.studentCode", target = "studentCode")
    @Mapping(source = "examEntry.id", target = "examEntryId")
    @Mapping(source = "exam.id", target = "examId")
    @Mapping(source = "correct", target = "correctCount")
    @Mapping(source = "incorrect", target = "incorrectCount")
    @Mapping(source = "unanswered", target = "unansweredCount")
    ExamResultDto toDto(ExamResult entity);

    List<ExamResultDto> toDtoList(List<ExamResult> entities);
}
