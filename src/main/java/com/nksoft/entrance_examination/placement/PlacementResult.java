package com.nksoft.entrance_examination.placement;

import com.nksoft.entrance_examination.department.model.Department;
import com.nksoft.entrance_examination.student.model.Student;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

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

    @OneToOne
    private Student student;
    @ManyToOne
    private Department department;

    @Column(nullable = false)
    private int rank;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
