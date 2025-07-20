package com.nksoft.entrance_examination.placement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.stream.Stream;

@Repository
public interface PlacementResultRepository extends JpaRepository<PlacementResult, Long> {
    List<PlacementResult> findByDepartment_IdOrderByFinalScoreDesc(Long departmentId);

    @Query("SELECT p FROM PlacementResult p ORDER BY p.department.id, p.finalScore DESC")
    Stream<PlacementResult> rankedByFinalScoreForDepartmentStream();
}
