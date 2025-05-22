package com.nksoft.entrance_examination.repository;

import com.nksoft.entrance_examination.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    @Modifying
    @Query("DELETE FROM Exam e WHERE e.id = :id")
    int deleteByIdReturningCount(@Param("id") Long id);
}
