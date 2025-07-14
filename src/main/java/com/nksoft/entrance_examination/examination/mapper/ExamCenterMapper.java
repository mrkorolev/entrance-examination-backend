package com.nksoft.entrance_examination.examination.mapper;

import com.nksoft.entrance_examination.examination.dto.ExamCenterDto;
import com.nksoft.entrance_examination.examination.model.ExamCenter;
import com.nksoft.entrance_examination.examination.model.ExamEntry;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ExamCenterMapper {
    @Mapping(source = "examCenterId", target = "id")
    ExamCenter toEntity(ExamCenterDto dto);

    @Mapping(source = "id", target = "examCenterId")
    @Mapping(source = "examEntries", target = "examEntryIds", qualifiedByName = "toExamEntryIds")
    ExamCenterDto toDto(ExamCenter entity);

    List<ExamCenterDto> toDtoList(List<ExamCenter> entities);

    @Named("toExamEntryIds")
    default List<Long> toExamEntryIds(List<ExamEntry> examEntries) {
        return examEntries.stream()
                .map(ExamEntry::getId)
                .toList();
    }
}
