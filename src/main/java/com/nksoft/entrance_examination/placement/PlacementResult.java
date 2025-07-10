package com.nksoft.entrance_examination.placement;

import com.nksoft.entrance_examination.department.model.Department;
import com.nksoft.entrance_examination.student.model.Student;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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
@Table(name = "placement_results")
public class PlacementResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "department_code")
    private Department department;
    @OneToOne
    @JoinColumn(name = "student_code", referencedColumnName = "student_code")
    private Student student;

    @Column(name = "placement_rank", nullable = false)
    private int rank;
}
