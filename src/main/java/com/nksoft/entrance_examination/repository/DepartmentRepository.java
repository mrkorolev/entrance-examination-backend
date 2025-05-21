package com.nksoft.entrance_examination.repository;

import com.nksoft.entrance_examination.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    boolean existsByName(String name);

    @Query(value = """
    SELECT unnest(:ids) AS missing_id
    EXCEPT
    SELECT id FROM departments
    """, nativeQuery = true)
    List<Long> findMissingDepartmentIds(List<Long> ids);
}
