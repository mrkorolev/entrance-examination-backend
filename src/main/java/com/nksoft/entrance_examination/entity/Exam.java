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
import java.time.LocalDateTime;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_center_id", nullable = false)
    private ExamCenter examCenter;

    @Enumerated(EnumType.STRING)
    @Column(name = "exam_type")
    private GradeType examType;
    @Column(name = "exam_date")
    private LocalDateTime examDate;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "total_questions")
    private int totalQuestions;
    @Column(name = "duration_minutes")
    private int durationInMinutes;
}
