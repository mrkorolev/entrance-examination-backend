package com.nksoft.entrance_examination.placement;

import com.nksoft.entrance_examination.examination.model.GradeType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PlacementResultDto {
    private Long placementId;
    private Long departmentId;
    private Long studentId;
    private GradeType gradeType;
    private Long finalScore;
}
