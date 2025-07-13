package com.nksoft.entrance_examination.examination.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "exam_results")
public class ExamResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "exam_entry_id", nullable = false)
    private ExamEntry examEntry;
    @ManyToOne
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @Enumerated(EnumType.STRING)
    @Column(name = "booklet_type", nullable = false)
    private BookletType bookletType;
    @Column(name = "raw_answers", nullable = false)
    private String rawAnswers;
    @Column(nullable = false)
    private int correct;
    @Column(nullable = false)
    private int incorrect;
    @Column(nullable = false)
    private int unanswered;

    @Column(name = "raw_score")
    private Float rawScore;
    @Column(name = "normalized_score")
    private Float normalizedScore;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
