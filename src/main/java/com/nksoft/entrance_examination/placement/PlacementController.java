package com.nksoft.entrance_examination.placement;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name  = "Placements", description = "Operations regarding placement results")
public class PlacementController {
    private final PlacementService service;
    private final PlacementMapper mapper;

    @Operation(summary = "Trigger placement", description = "Triggers placement algorithm and persists it's final state")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful retrieval of student for a provided ID"),
            @ApiResponse(responseCode = "400", description = """
            Failed to run the department placement algorithm. Will happen if:
            - placement algorithm has recently been triggered (db contents were altered)
            - not all exams were conducted at the moment of triggering the algorithm
            - statistical params can't be calculated (exam results for some exam are absent)""")})
    @GetMapping("/trigger")
    public ResponseEntity<?> triggerPlacements() {
        service.runPlacementAlgorithm();
        return ResponseEntity.ok("Placement algorithm was executed successfully");
    }

    @Operation(summary = "Get placements for department", description = "Returns a list of placement results for provided department ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful retrieval of placement results for a provided department ID"),
            @ApiResponse(responseCode = "404", description = "No department found for provided department ID")})
    @GetMapping
    public List<PlacementResultDto> getPlacementsForDepartment(
            @RequestParam(name = "department-id") Long departmentId
    ) {
        List<PlacementResult> placementResults = service.findPlacementsForDepartment(departmentId);
        return mapper.toDtoList(placementResults);
    }

    @Operation(summary = "Export placements", description = "Exports placement results to a txt file")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully exported placement results to placements.txt")})
    @GetMapping("/export")
    public ResponseEntity<ByteArrayResource> exportPlacements() {
        return service.exportPlacementsToTxt();
    }
}