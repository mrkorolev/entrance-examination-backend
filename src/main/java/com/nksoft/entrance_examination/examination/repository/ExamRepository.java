package com.nksoft.entrance_examination.examination.repository;

import com.nksoft.entrance_examination.examination.model.Exam;
import com.nksoft.entrance_examination.examination.model.GradeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    @Modifying
    @Query("DELETE FROM Exam e WHERE e.id = :id")
    int deleteByIdReturningCount(@Param("id") Long id);

    boolean existsByGradeType(GradeType gradeType);

    boolean existsByStartTimeBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);
}
