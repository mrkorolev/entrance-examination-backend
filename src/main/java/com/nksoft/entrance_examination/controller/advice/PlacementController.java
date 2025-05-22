package com.nksoft.entrance_examination.controller.advice;

import com.nksoft.entrance_examination.service.PlacementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/placements")
public class PlacementController {
    private final PlacementService placementService;

    @PostMapping("/trigger")
    public ResponseEntity<?> triggerPlacementAlgorithm() {
        return placementService.triggerDepartmentPlacementAlgorithm();
    }
}
