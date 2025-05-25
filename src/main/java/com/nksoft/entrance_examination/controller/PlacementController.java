package com.nksoft.entrance_examination.controller;

import com.nksoft.entrance_examination.service.PlacementService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/department-placements")
public class PlacementController {
    private final PlacementService placementService;

    @GetMapping("/trigger")
    public ResponseEntity<ByteArrayResource> triggerPlacementAlgorithm() {
        ByteArrayResource reportFile = placementService.runPlacement();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"placements.txt\"")
                .contentType(MediaType.TEXT_PLAIN)
                .contentLength(reportFile.contentLength())
                .body(reportFile);
    }
}
