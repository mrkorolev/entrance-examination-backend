package com.nksoft.entrance_examination.student.controller;

import com.nksoft.entrance_examination.common.mapper.StudentMapper;
import com.nksoft.entrance_examination.student.model.Student;
import com.nksoft.entrance_examination.student.dto.StudentDto;
import com.nksoft.entrance_examination.student.StudentService;
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
import org.springframework.web.bind.annotation.PutMapping;
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
@RequestMapping("/students")
@Tag(name  = "Student", description = "Operations regarding student management")
public class StudentController {
    private final StudentService service;
    private final StudentMapper mapper;

    @Operation(summary = "Get students", description = "Returns a list of students (based on ids or pagination provided)")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Successful retrieval of students"))
    @GetMapping
    public ResponseEntity<?> getStudents(
            @RequestParam(name = "student-codes", required = false) List<Long> studentCodes,
            @PageableDefault(size = 20, sort = "studentCode") Pageable pageable
    ) {
        if (studentCodes == null || studentCodes.isEmpty()) {
            Page<Student> foundStudents = service.findStudents(pageable);
            return ResponseEntity.ok(foundStudents.map(mapper::toDto));
        } else {
            List<Student> foundStudents = service.findStudentsByCodes(studentCodes);
            return ResponseEntity.ok(mapper.toDtoList(foundStudents));
        }
    }

    @Operation(summary = "Get student by code", description = "Returns a single student with a unique code")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful retrieval of student with a provided code"),
            @ApiResponse(responseCode = "404", description = "No student found for provided code")})
    @GetMapping("/{code}")
    public StudentDto getStudentByCode(@PathVariable Long code) {
        Student found = service.findStudentByCode(code);
        return mapper.toDto(found);
    }

    @Operation(summary = "Register student", description = "Registers and returns a new student")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Successful creation of student with a provided dto"),
            @ApiResponse(responseCode = "400", description = "Student with provided email already exists")})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public StudentDto addNewStudent(@Valid @RequestBody StudentDto dto) {
        Student toRegister = mapper.toEntity(dto);
        Student registered = service.registerStudent(toRegister);
        return mapper.toDto(registered);
    }

    @Operation(summary = "Update student's department preferences",
               description = "Updates department preferences of a student with provided code and returns him")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful update of student department preferences with a provided code"),
            @ApiResponse(responseCode = "400", description = """
            Failed to update department preferences for a student with provided code"). Happens if:"
            - some of the departments with provided codes do not exist
            - student for provided code doesn't exist""")})
    @PutMapping("/{code}")
    public StudentDto setStudentDepartmentPreferences(@PathVariable Long code,
                                                      @RequestBody List<Long> departmentIds) {
        Student updated = service.updateDepartmentPreferences(code, departmentIds);
        return mapper.toDto(updated);
    }

    @Operation(summary = "Remove student", description = "Removes a single student with a unique code")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Successful removal of student with a provided code"),
            @ApiResponse(responseCode = "404", description = "No student found for provided code")})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{code}")
    public void deleteStudentById(@PathVariable Long code) {
        service.removeStudentByCode(code);
    }

    @Operation(summary = "Student batch insert", description = "Registers students based on delimiter & info provided in a batch file")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Successful creation of departments from a batch file"),
            @ApiResponse(responseCode = "400", description = """
            An error occurred while parsing through the batch file. Happens if:
            - provided student file is empty"
            - error happened while parsing the file (e.g. file format/delimiter is invalid)"""),
            @ApiResponse(responseCode = "500", description = "I/O error while opening/closing the file")})
    @PostMapping("/testing-batch-upload")
    public ResponseEntity<?> uploadStudentsBatch(@RequestBody MultipartFile file,
                                                 @RequestParam(defaultValue = " ") String delimiter,
                                                 @RequestParam(defaultValue = "50") int batchSize) throws IOException {
        service.processBatchFile(file, delimiter, batchSize);
        return ResponseEntity.ok("Successfully processed students batch file");
    }

    @Operation(summary = "Export students", description = "Exports students to a csv batch file")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully exported students to a csv batch file")})
    @GetMapping("/export")
    public ResponseEntity<ByteArrayResource> exportStudents() {
        return service.exportStudentsToCsv();
    }
}