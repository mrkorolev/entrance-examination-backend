package com.nksoft.entrance_examination.entity;

import com.vladmihalcea.hibernate.type.array.LongArrayType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import org.hibernate.annotations.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_code", nullable = false, unique = true)
    private Long studentCode;

    @OneToMany(
            mappedBy = "student",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<ExamEntry> examEntries = new ArrayList<>();

    @Column(length = 64, nullable = false)
    private String name;
    @Column(length = 64, unique = true, nullable = false)
    private String email;
    @Column(name = "password_encrypted", length = 64, nullable = false)
    private String password;

    @Column(name = "department_preferences", columnDefinition = "bigint[]")
    @Type(LongArrayType.class)
    private Long[] departmentPreferences;
    @Column(name = "placed_preference_idx")
    private Integer placedPreferenceIdx;
    @Column(nullable = false)
    private Float cgpa;
    @Column(name = "grade1_result")
    private Float grade1Result;
    @Column(name = "grade2_result")
    private Float grade2Result;
    @Column(name = "grade3_result")
    private Float grade3Result;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}