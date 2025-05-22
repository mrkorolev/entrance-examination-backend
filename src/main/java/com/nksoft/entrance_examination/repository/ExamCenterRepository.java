package com.nksoft.entrance_examination.repository;

import com.nksoft.entrance_examination.entity.ExamCenter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamCenterRepository extends JpaRepository<ExamCenter, Long> {
    boolean existsByName(String name);

    @Modifying
    @Query("DELETE FROM ExamCenter ctr WHERE ctr.id = :id")
    int deleteByIdReturningCount(@Param("id") Long id);
}
