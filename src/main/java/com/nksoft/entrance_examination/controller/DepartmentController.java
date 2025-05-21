package com.nksoft.entrance_examination.controller;

import com.nksoft.entrance_examination.dto.DepartmentDto;
import com.nksoft.entrance_examination.entity.Department;
import com.nksoft.entrance_examination.mapper.DepartmentMapper;
import com.nksoft.entrance_examination.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/departments")
@Tag(name = "Department", description = "Operations related to department management")
public class DepartmentController {
    private final DepartmentService depService;
    private final DepartmentMapper depMapper;

    // TODO: add pagination, potentially ~10^3 records
    @Operation(summary = "Get departments", description = "Returns a list of departments")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Successful retrieval of departments"))
    @GetMapping
    public List<DepartmentDto> getDepartments() {
        List<Department> foundDepartments = depService.findDepartments();
        return depMapper.toDtoList(foundDepartments);
    }

    @Operation(summary = "Get department by ID", description = "Returns a single department with a unique ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful retrieval of department with a provided ID"),
            @ApiResponse(responseCode = "404", description = "No department found for provided ID")})
    @GetMapping("/{id}")
    public DepartmentDto getDepartmentById(@PathVariable Long id) {
        Department found = depService.findDepartmentById(id);
        return depMapper.toDto(found);
    }

    @Operation(summary = "Register department", description = "Registers and returns a new department")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Successful creation of department with a provided dto"),
            @ApiResponse(responseCode = "400", description = "Department with provided name already exists"),
            @ApiResponse(responseCode = "404", description = "University with ID provided in the dto doesn't exist")})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public DepartmentDto createDepartment(@RequestBody DepartmentDto dto) {
        Department toRegister = depMapper.toEntity(dto);
        Department registered = depService.registerDepartment(toRegister);
        return depMapper.toDto(registered);
    }

    @Operation(summary = "Department batch insert", description = "Registers departments based delimiter & info provided in a batch file")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful creation of departments from a batch file"),
            @ApiResponse(responseCode = "400", description = """
            An error occurred while parsing through the batch file. Happens if:
            - provided department file is empty"
            - error happened while parsing the file (e.g. file format/delimiter is invalid)"""),
            @ApiResponse(responseCode = "404", description = "University with ID provided in the dto doesn't exist"),
            @ApiResponse(responseCode = "500", description = "I/O error while opening/closing the file")})
    @PostMapping("/testing-batch-upload")
    public ResponseEntity<?> uploadDepartmentsBatch(@RequestBody MultipartFile file,
                                                    @RequestParam(defaultValue = " ") String delimiter) throws IOException {
        depService.processBatchFile(file, delimiter);
        return ResponseEntity.ok("Successfully processed departments the batch file");
    }

    @Operation(summary = "Remove department", description = "Removes a single department with a unique ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Successful removal of department with a provided ID"),
            @ApiResponse(responseCode = "404", description = "No department found for provided ID")})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteDepartmentById(@PathVariable Long id) {
        depService.removeDepartmentById(id);
    }
}
