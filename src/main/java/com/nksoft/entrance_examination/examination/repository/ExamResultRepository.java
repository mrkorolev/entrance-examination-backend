package com.nksoft.entrance_examination.examination.repository;

import com.nksoft.entrance_examination.examination.model.ExamResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ExamResultRepository extends JpaRepository<ExamResult, Long> {
    @Query("""
    SELECT er.netScore
    FROM ExamResult er
    WHERE er.examEntry.id = :examEntryId
    """)
    List<Float> findNetScoresByGradeType(@Param("examEntryId") Long examEntryId);
}
