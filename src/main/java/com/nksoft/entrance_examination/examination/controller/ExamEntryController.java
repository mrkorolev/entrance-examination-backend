package com.nksoft.entrance_examination.examination.controller;

import com.nksoft.entrance_examination.examination.dto.ExamEntryDto;
import com.nksoft.entrance_examination.examination.model.ExamEntry;
import com.nksoft.entrance_examination.examination.mapper.ExamEntryMapper;
import com.nksoft.entrance_examination.examination.service.ExamEntryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/exam-entries")
@Tag(name = "Exam Entry", description = "Operations related to exam entry management")
public class ExamEntryController {
    private final ExamEntryService service;
    private final ExamEntryMapper mapper;

    @Operation(summary = "Get exam entries", description = "Returns a list of entries if entry IDs were provided (otherwise returns a Page)")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Successful retrieval of exam entries"))
    @GetMapping
    public ResponseEntity<?> getEntries(
            @RequestParam(name = "entry-ids", required = false) List<Long> entryIds,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        if (entryIds == null || entryIds.isEmpty()) {
            Page<ExamEntry> foundEntries = service.findEntries(pageable);
            return ResponseEntity.ok(foundEntries.map(mapper::toDto));
        } else {
            List<ExamEntry> foundEntries = service.findEntriesByIds(entryIds);
            return ResponseEntity.ok(mapper.toDtoList(foundEntries));
        }
    }

    @Operation(summary = "Get exam entry by ID", description = "Returns a single exam entry with a unique ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful retrieval of exam entry for provided ID"),
            @ApiResponse(responseCode = "404", description = "No exam entry found for provided ID")})
    @GetMapping("/{id}")
    public ExamEntryDto getEntryById(@PathVariable Long id) {
        ExamEntry found = service.findEntryById(id);
        return mapper.toDto(found);
    }

    @Operation(summary = "Register exam entry", description = "Registers and returns a new exam entry")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Successful creation of exam entry with a provided dto"),
            @ApiResponse(responseCode = "400", description = "Exam center registrations are full or student is already registered in a different center"),
            @ApiResponse(responseCode = "404", description = """
            Failed to register a new exam entry. Happens if:
            - student for provided ID doesn't exist
            - exam for provided ID doesn't exist""")})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ExamEntryDto createEntry(@Valid @RequestBody ExamEntryDto dto) {
        ExamEntry toRegister = mapper.toEntity(dto);
        ExamEntry registered = service.registerEntry(toRegister);
        return mapper.toDto(registered);
    }

    @Operation(summary = "Remove exam entry", description = "Removes a single exam entry with a unique ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Successful removal of exam entry for a provided ID"),
            @ApiResponse(responseCode = "404", description = "No exam entry found for provided ID")})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteEntryById(@PathVariable Long id) {
        service.removeEntryById(id);
    }

    @Operation(summary = "Export entries", description = "Exports entries to a csv file")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully exported exam entries to exam_entries.csv")})
    @GetMapping("/export")
    public ResponseEntity<ByteArrayResource> exportEntries(
            @RequestParam(required = false) Long examCenterId) {
        return service.exportEntriesToCsv(examCenterId);
    }
}
