package com.nksoft.entrance_examination.placement;

import com.nksoft.entrance_examination.common.mapper.PlacementMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/placements")
public class PlacementController {
    private final PlacementService service;
    private final PlacementMapper mapper;

    @GetMapping("/trigger")
    public ResponseEntity<ByteArrayResource> triggerPlacementAlgorithmAndExport() {
        return service.runPlacementAndExport();
    }

    // TODO: add documentation info
    @GetMapping
    public List<PlacementResultDto> getPlacements(
            @RequestParam(name = "department-id") Long departmentId
    ) {
        List<PlacementResult> placementResults = service.findPlacementsForDepartment(departmentId);
        return mapper.toDtoList(placementResults);
    }

    // TODO: add export endpoint
    @GetMapping("/export")
    public ResponseEntity<ByteArrayResource> exportPlacements() {
        return null;
    }
}