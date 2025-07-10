package com.nksoft.entrance_examination.department.model;

import com.nksoft.entrance_examination.examination.model.GradeType;
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
    private Long id;

    // Default for ManyToOne relation: FetchType.EAGER
    @ManyToOne
    @JoinColumn(name = "university_id")
    private University university;

    @Column(length = 64, nullable = false, unique = true)
    private String name;
    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_grade", nullable = false)
    private GradeType preferredGrade;
    @Column(nullable = false)
    private int quota;
}
