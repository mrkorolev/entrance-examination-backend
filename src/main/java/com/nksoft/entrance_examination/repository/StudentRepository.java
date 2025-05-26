package com.nksoft.entrance_examination.repository;

import com.nksoft.entrance_examination.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    boolean existsByEmail(String email);

    @Modifying
    @Query("DELETE FROM Student s WHERE s.id = :id")
    int deleteByIdReturningCount(@Param("id") Long id);

    Optional<Student> findByEmail(String email);

//    List<Student> findAllByPlacedPreferenceIdx(Integer placedPreferenceIdx);
    List<Student> findAllByPlacedPreferenceIdxOrderByName(Integer placedPreferenceIdx);
}
