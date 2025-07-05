package com.nksoft.entrance_examination.department.controller;

import com.nksoft.entrance_examination.common.mapper.DepartmentMapper;
import com.nksoft.entrance_examination.department.model.Department;
import com.nksoft.entrance_examination.department.dto.DepartmentDto;
import com.nksoft.entrance_examination.department.service.DepartmentService;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/departments")
@Tag(name = "Department", description = "Operations related to department management")
public class DepartmentController {
    private final DepartmentService service;
    private final DepartmentMapper mapper;

    @Operation(summary = "Get departments", description = "Returns a list of departments")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Successful retrieval of departments"))
    @GetMapping
    public ResponseEntity<?> getDepartments(
            @RequestParam(required = false) List<Long> departmentCodes,
            @PageableDefault(size = 20, sort = "departmentCode") Pageable pageable
    ) {
        if (departmentCodes == null || departmentCodes.isEmpty()) {
            Page<Department> foundDepartments = service.findDepartments(pageable);
            return ResponseEntity.ok(foundDepartments.map(mapper::toDto));
        } else {
            List<Department> foundDepartments = service.findDepartmentsByCodes(departmentCodes);
            return ResponseEntity.ok(mapper.toDtoList(foundDepartments));
        }
    }

    @Operation(summary = "Get department by code", description = "Returns a single department with a unique code")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful retrieval of department with a provided code"),
            @ApiResponse(responseCode = "404", description = "No department found for provided code")})
    @GetMapping("/{code}")
    public DepartmentDto getDepartmentById(@PathVariable Long code) {
        Department found = service.findDepartmentByCode(code);
        return mapper.toDto(found);
    }

    @Operation(summary = "Register department", description = "Registers and returns a new department")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Successful creation of department with a provided dto"),
            @ApiResponse(responseCode = "400", description = "Department with provided name already exists"),
            @ApiResponse(responseCode = "404", description = "University with ID provided in the dto doesn't exist")})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public DepartmentDto createDepartment(@Valid @RequestBody DepartmentDto dto) {
        Department toRegister = mapper.toEntity(dto);
        Department registered = service.registerDepartment(toRegister);
        return mapper.toDto(registered);
    }

    @Operation(summary = "Remove department", description = "Removes a single department with a unique code")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Successful removal of department with a provided code"),
            @ApiResponse(responseCode = "404", description = "No department found for provided code")})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{code}")
    public void deleteDepartmentById(@PathVariable Long code) {
        service.removeDepartmentById(code);
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
                                                    @RequestParam(defaultValue = " ") String delimiter,
                                                    @RequestParam(defaultValue = "25") int batchSize) throws IOException {
        service.processBatchFile(file, delimiter, batchSize);
        return ResponseEntity.ok("Successfully processed departments the batch file");
    }

    @Operation(summary = "Export departments", description = "Exports departments to a csv batch file")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully exported departments to a csv batch file")})
    @GetMapping("/export")
    public ResponseEntity<ByteArrayResource> exportDepartments() {
        return service.exportDepartmentsToCsv();
    }
}
