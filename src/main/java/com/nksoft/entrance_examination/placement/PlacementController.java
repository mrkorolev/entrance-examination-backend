package com.nksoft.entrance_examination.placement;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/placements")
public class PlacementController {
    private final PlacementService placementService;

    @GetMapping("/trigger")
    public ResponseEntity<ByteArrayResource> triggerPlacementAlgorithm() {
        return placementService.runPlacement();
    }
}