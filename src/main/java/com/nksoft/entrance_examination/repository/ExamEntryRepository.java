package com.nksoft.entrance_examination.repository;

import com.nksoft.entrance_examination.entity.ExamEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamEntryRepository extends JpaRepository<ExamEntry, Long> {
}
