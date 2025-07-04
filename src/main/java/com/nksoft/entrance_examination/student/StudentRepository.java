package com.nksoft.entrance_examination.student;

import com.nksoft.entrance_examination.student.model.Student;
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
    @Query("DELETE FROM Student s WHERE s.studentCode = :studentCode")
    int deleteByIdReturningCount(@Param("studentCode") Long studentCode);

    Optional<Student> findByEmail(String email);

    List<Student> findAllByPlacedPreferenceIdxOrderByName(Integer placedPreferenceIdx);
}
