package com.nksoft.entrance_examination.mapper;

import com.nksoft.entrance_examination.dto.ExamCenterDto;
import com.nksoft.entrance_examination.entity.Exam;
import com.nksoft.entrance_examination.entity.ExamCenter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ExamCenterMapper {
    @Mapping(source = "examCenterId", target = "id")
    @Mapping(source = "capacity", target = "capacity", defaultValue = "100")
    ExamCenter toEntity(ExamCenterDto dto);

    @Mapping(source = "id", target = "examCenterId")
    @Mapping(source = "exams", target = "examIds", qualifiedByName = "toExamIds")
    ExamCenterDto toDto(ExamCenter entity);

    List<ExamCenterDto> toDtoList(List<ExamCenter> entities);

    @Named("toExamIds")
    default List<Long> toExamIds(List<Exam> exams) {
        return exams.stream()
                .map(Exam::getId)
                .toList();
    }
}
