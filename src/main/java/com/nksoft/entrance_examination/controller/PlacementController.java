package com.nksoft.entrance_examination.controller;

import com.nksoft.entrance_examination.service.PlacementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/placements")
public class PlacementController {
    private final PlacementService placementService;

    @GetMapping("/trigger")
    public ResponseEntity<?> triggerPlacementAlgorithm() {
        placementService.runPlacement();
        return ResponseEntity.ok("Placement algorithm finished processing");
    }
}
