package com.nksoft.entrance_examination.examination.repository;

import com.nksoft.entrance_examination.examination.model.ExamCenter;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamCenterRepository extends JpaRepository<ExamCenter, Long> {
    boolean existsByName(String name);

    @Query(nativeQuery = true, value = """
    SELECT ec.*
    FROM exam_centers ec
    LEFT JOIN exam_entries ee ON ec.id = ee.exam_center_id
    GROUP BY ec.id, ec.name, ec.total_rooms, ec.room_capacity
    HAVING COUNT(ee.id) < ec.total_rooms * ec.room_capacity""")
    List<ExamCenter> findAvailable();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM ExamCenter c WHERE c.id = :id")
    Optional<ExamCenter> findLockedById(@Param("id") Long id);

    @Modifying
    @Query("DELETE FROM ExamCenter ctr WHERE ctr.id = :id")
    int deleteByIdReturningCount(@Param("id") Long id);
}
