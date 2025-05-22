package com.nksoft.entrance_examination.repository;

import com.nksoft.entrance_examination.entity.University;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UniversityRepository extends JpaRepository<University, Long> {
    boolean existsByName(String name);

    @Modifying
    @Query("DELETE FROM University u WHERE u.id = :id")
    int deleteByIdReturningCount(@Param("id") Long id);
}
