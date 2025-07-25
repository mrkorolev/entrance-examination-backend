package com.nksoft.entrance_examination.examination.repository;

import com.nksoft.entrance_examination.examination.model.ExamEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamEntryRepository extends JpaRepository<ExamEntry, Long> {
    @Modifying
    @Query("DELETE FROM ExamEntry etr WHERE etr.id = :id")
    int deleteByIdReturningCount(@Param("id") Long id);

    int countByExamCenter_Id(Long id);

    boolean existsByStudent_Id(Long studentId);

    Optional<ExamEntry> findByStudent_Id(Long studentId);

    List<ExamEntry> findByExamCenter_Id(Long centerId);
}
