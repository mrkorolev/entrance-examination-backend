package com.nksoft.entrance_examination.entity;

import lombok.Getter;

@Getter
// TODO: consider private lombok constructor
public enum GradeType {
    GRADE1("TYT", 100),
    GRADE2("AYT", 120),
    GRADE3("YDT", 140);
    private final String gradeName;
    private final int totalQuestions;

    GradeType(String gradeName, int totalQuestions) {
        this.gradeName = gradeName;
        this.totalQuestions = totalQuestions;
    }
}
