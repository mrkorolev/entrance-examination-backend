package com.nksoft.entrance_examination.placement;

import com.nksoft.entrance_examination.department.model.Department;
import com.nksoft.entrance_examination.examination.model.GradeType;
import com.nksoft.entrance_examination.student.model.Student;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "placement_results",
   indexes = @Index(name = "idx_department_code", columnList = "department_code"),
   uniqueConstraints = @UniqueConstraint(columnNames = {"student_id"})
)
public class PlacementResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "department_id",
                nullable = false)
    private Department department;
    @OneToOne
    @JoinColumn(name = "student_id",
                referencedColumnName = "id",
                nullable = false)
    private Student student;

    @Column(name  = "final_score", nullable = false)
    private Float finalScore;
    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_by_department", nullable = false)
    private GradeType grade;
}