package com.nksoft.entrance_examination.examination.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "exams")
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(
            mappedBy = "exam",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<ExamResult> results = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "exam_grade_type", nullable = false)
    private GradeType gradeType;
    @Column(name = "exam_date_time", nullable = false)
    private LocalDateTime startTime;
    @Column(name = "duration_minutes", nullable = false)
    private int durationInMinutes;

    @Column(name = "a_booklet_keys")
    private String bookletAKeys;
    @Column(name = "b_booklet_keys")
    private String bookletBKeys;
    @Column(name = "c_booklet_keys")
    private String bookletCKeys;

    private Float mean;
    private Float standardDeviation;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
