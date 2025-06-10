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
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "exams")
public class Exam {
    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "grade_type")
    private GradeType gradeType;

    @Column(name = "duration_minutes", nullable = false)
    private int durationInMinutes;
    @Column(name = "exam_start_time", nullable = false)
    private LocalDateTime startTime;
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "booklet_a_keys")
    private String bookletAKeys;
    @Column(name = "booklet_b_keys")
    private String bookletBKeys;
    @Column(name = "booklet_c_keys")
    private String bookletCKeys;
}
