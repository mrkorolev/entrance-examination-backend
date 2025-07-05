package com.nksoft.entrance_examination.department.repository;

import com.nksoft.entrance_examination.department.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    boolean existsByName(String name);

    @Query(nativeQuery = true, value = """
    SELECT unnest(:codes) AS missing_id
    EXCEPT
    SELECT id FROM departments""")
    List<Long> findMissingDepartmentCodes(List<Long> codes);

    @Modifying
    @Query("DELETE FROM Department d WHERE d.id = :id")
    int deleteByIdReturningCount(@Param("id") Long id);
}
