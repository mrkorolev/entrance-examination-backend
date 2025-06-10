package com.nksoft.entrance_examination.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "departments")
public class Department {
    @Id
    @Column(name = "department_code", nullable = false, unique = true)
    private Long departmentCode;

    // Default for ManyToOne relation: FetchType.EAGER
    @ManyToOne
    @JoinColumn(name = "university_id")
    private University university;

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_grade", nullable = false)
    private GradeType preferredGrade;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private int quota = 30;
}
