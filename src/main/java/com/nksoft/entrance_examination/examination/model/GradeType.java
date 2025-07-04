package com.nksoft.entrance_examination.examination.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GradeType {
    GRADE1("TYT"),
    GRADE2("AYT"),
    GRADE3("YDT");
    private final String gradeName;
}
