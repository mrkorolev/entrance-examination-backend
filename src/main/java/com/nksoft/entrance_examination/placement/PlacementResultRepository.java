package com.nksoft.entrance_examination.placement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlacementResultRepository extends JpaRepository<PlacementResult, Long> {}
