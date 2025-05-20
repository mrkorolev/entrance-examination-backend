package com.nksoft.entrance_examination.repository;

import com.nksoft.entrance_examination.entity.ExamCenter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamCenterRepository extends JpaRepository<ExamCenter, Long> {
    boolean existsByName(String name);
}
