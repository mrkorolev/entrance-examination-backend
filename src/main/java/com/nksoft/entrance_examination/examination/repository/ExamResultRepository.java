package com.nksoft.entrance_examination.examination.repository;

import com.nksoft.entrance_examination.examination.model.ExamResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamResultRepository extends JpaRepository<ExamResult, Long> {}
