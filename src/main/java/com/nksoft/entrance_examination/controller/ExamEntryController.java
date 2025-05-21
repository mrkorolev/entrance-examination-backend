package com.nksoft.entrance_examination.controller;

import com.nksoft.entrance_examination.dto.ExamEntryDto;
import com.nksoft.entrance_examination.entity.ExamEntry;
import com.nksoft.entrance_examination.mapper.ExamEntryMapper;
import com.nksoft.entrance_examination.service.ExamEntryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/exam-entries")
@Tag(name = "Exam Entry", description = "Operations related to exam entry management")
public class ExamEntryController {
    private final ExamEntryService entryService;
    private final ExamEntryMapper entryMapper;

    // TODO: add pagination, potentially ~10^6 records
    @Operation(summary = "Get exam entries", description = "Returns a list of exam entries")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Successful retrieval of exam entries"))
    @GetMapping
    public List<ExamEntryDto> getEntries() {
        List<ExamEntry> examEntries = entryService.findEntries();
        return entryMapper.toDtoList(examEntries);
    }

    @Operation(summary = "Get exam entry by ID", description = "Returns a single exam entry with a unique ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful retrieval of exam entry with a provided ID"),
            @ApiResponse(responseCode = "404", description = "No exam entry found for provided ID")})
    @GetMapping("/{id}")
    public ExamEntryDto getEntryById(@PathVariable Long id) {
        ExamEntry found = entryService.findEntryById(id);
        return entryMapper.toDto(found);
    }

    @Operation(summary = "Register exam entry", description = "Registers and returns a new exam entry")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Successful creation of exam entry with a provided dto"),
            @ApiResponse(responseCode = "404", description = """
            Failed to register a new exam entry. Happens if:
            - student for provided ID doesn't exist
            - exam for provided ID doesn't exist""")})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ExamEntryDto createEntry(@RequestBody ExamEntryDto dto) {
        ExamEntry toRegister = entryMapper.toEntity(dto);
        ExamEntry registered = entryService.registerEntry(toRegister);
        return entryMapper.toDto(registered);
    }

    @Operation(summary = "Remove exam entry", description = "Removes a single exam entry with a unique ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Successful removal of exam entry with a provided ID"),
            @ApiResponse(responseCode = "404", description = "No exam entry found for provided ID")})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteEntryById(@PathVariable Long id) {
        entryService.removeEntryById(id);
    }
}
