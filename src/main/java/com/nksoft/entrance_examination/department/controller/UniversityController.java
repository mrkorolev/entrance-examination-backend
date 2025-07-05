package com.nksoft.entrance_examination.department.controller;

import com.nksoft.entrance_examination.common.mapper.UniversityMapper;
import com.nksoft.entrance_examination.department.model.University;
import com.nksoft.entrance_examination.department.dto.UniversityDto;
import com.nksoft.entrance_examination.department.service.UniversityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@RequestMapping("/universities")
@Tag(name = "University", description = "Operations related to university management")
public class UniversityController {
    private final UniversityService service;
    private final UniversityMapper mapper;

    @Operation(summary = "Get universities", description = "Returns a list of universities")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Successful retrieval of universities"))
    @GetMapping
    public List<UniversityDto> getUniversities() {
        List<University> foundUniversities = service.findUniversities();
        return mapper.toDtoList(foundUniversities);
    }

    @Operation(summary = "Get university by ID", description = "Returns a single university with a unique ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successful retrieval of university with a provided ID"),
        @ApiResponse(responseCode = "404", description = "No university found for provided ID")})
    @GetMapping("/{id}")
    public UniversityDto getUniversityById(@PathVariable Long id) {
        University found = service.findUniversityById(id);
        return mapper.toDto(found);
    }

    @Operation(summary = "Register university", description = "Registers and returns a new university")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Successful creation of university with a provided dto"),
            @ApiResponse(responseCode = "400", description = "University with provided name already exists")})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public UniversityDto addNewUniversity(@Valid @RequestBody UniversityDto dto) {
        University toRegister = mapper.toEntity(dto);
        University registered = service.registerUniversity(toRegister);
        return mapper.toDto(registered);
    }

    @Operation(summary = "Remove university", description = "Removes a single university with a unique ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Successful removal of university with a provided ID"),
        @ApiResponse(responseCode = "404", description = "No university found for provided ID")})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteUniversityById(@PathVariable Long id) {
        service.removeUniversityById(id);
    }
}
