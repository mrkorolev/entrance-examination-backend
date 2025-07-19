package com.nksoft.entrance_examination.student;

import com.nksoft.entrance_examination.student.model.Student;
import com.nksoft.entrance_examination.student.model.StudentStatus;
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

    List<Student> findByStatus(StudentStatus status);

    List<Student> findAllByPlacedPreferenceIdxOrderByName(Integer placedPreferenceIdx);

    @Query("SELECT s.id FROM Student s WHERE s.id IN :ids")
    List<Long> findExistingIds(List<Long> ids);
}
