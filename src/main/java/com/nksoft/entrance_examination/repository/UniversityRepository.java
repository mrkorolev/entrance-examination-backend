package com.nksoft.entrance_examination.repository;

import com.nksoft.entrance_examination.entity.University;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UniversityRepository extends JpaRepository<University, Long> {
}
