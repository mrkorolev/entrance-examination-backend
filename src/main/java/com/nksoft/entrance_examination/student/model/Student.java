package com.nksoft.entrance_examination.student.model;

import com.nksoft.entrance_examination.examination.model.ExamEntry;
import com.vladmihalcea.hibernate.type.array.LongArrayType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "students")
public class Student {
    @Id
    private Long id;
    @OneToOne(mappedBy = "student", cascade = CascadeType.REMOVE)
    private ExamEntry examEntry;
    // TODO: check for recursion in Jackson serialization

    @Column(length = 64, nullable = false)
    private String name;
    @Column(length = 64, unique = true, nullable = false)
    private String email;
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
    @Enumerated(EnumType.STRING)
    private StudentStatus status;

    @Column(name = "preferred_dep_ids", columnDefinition = "bigint[]")
    @Type(LongArrayType.class)
    private Long[] preferredDepartmentIds;
    @Column(name = "placed_pref_idx")
    private Integer placedPreferenceIdx;

    @Column(nullable = false)
    private Float cgpa;
    @Column(name = "grade1_result")
    private Float grade1Result = 0F;
    @Column(name = "grade2_result")
    private Float grade2Result = 0F;
    @Column(name = "grade3_result")
    private Float grade3Result = 0F;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}