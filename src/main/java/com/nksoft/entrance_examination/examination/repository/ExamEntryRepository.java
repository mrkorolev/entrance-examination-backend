package com.nksoft.entrance_examination.examination.repository;

import com.nksoft.entrance_examination.examination.model.ExamEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamEntryRepository extends JpaRepository<ExamEntry, Long> {
    @Modifying
    @Query("DELETE FROM ExamEntry etr WHERE etr.id = :id")
    int deleteByIdReturningCount(@Param("id") Long id);
}
