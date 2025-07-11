package com.nksoft.entrance_examination.placement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PlacementResultRepository extends JpaRepository<PlacementResult, Long> {
    List<PlacementResult> findByDepartment_IdOrderByRankDesc(Long departmentId);
}
