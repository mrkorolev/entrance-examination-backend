package com.nksoft.entrance_examination.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Default for ManyToOne relation: FetchType.EAGER
    @ManyToOne
    @JoinColumn(name = "university_id")
    private University university;

    private String name;
    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_grade", nullable = false)
    private GradeType preferredGrade;
    private int quota;

    // TODO: think about how to save student preferences (separate table or else...)
}
