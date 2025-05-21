package com.nksoft.entrance_examination.controller;

import com.nksoft.entrance_examination.dto.StudentDto;
import com.nksoft.entrance_examination.entity.Student;
import com.nksoft.entrance_examination.mapper.StudentMapper;
import com.nksoft.entrance_examination.service.StudentService;
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
    private final StudentService studentService;
    private final StudentMapper studentMapper;

    // TODO: add pagination, potentially ~10^6 records
    @Operation(summary = "Get students", description = "Returns a list of students")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Successful retrieval of students"))
    @GetMapping
    public List<StudentDto> getStudents() {
        List<Student> foundStudents = studentService.findStudents();
        return studentMapper.toDtoList(foundStudents);
    }

    @Operation(summary = "Get student by ID", description = "Returns a single student with a unique ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful retrieval of student with a provided ID"),
            @ApiResponse(responseCode = "404", description = "No student found for provided ID")})
    @GetMapping("/{id}")
    public StudentDto getStudentById(@PathVariable Long id) {
        Student found = studentService.findStudentById(id);
        return studentMapper.toDto(found);
    }

    @Operation(summary = "Register student", description = "Registers and returns a new student")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Successful creation of student with a provided dto"),
            @ApiResponse(responseCode = "400", description = "Student with provided email already exists")})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public StudentDto addNewStudent(@RequestBody StudentDto dto) {
        Student toRegister = studentMapper.toEntity(dto);
        Student registered = studentService.registerStudent(toRegister);
        return studentMapper.toDto(registered);
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
                                                 @RequestParam(defaultValue = " ") String delimiter) throws IOException {
        studentService.processBatchFile(file, delimiter);
        return ResponseEntity.ok("Successfully processed students batch file");
    }

    @Operation(summary = "Update student's department preferences",
               description = "Updates department preferences of a student with provided ID and returns him")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful update of student department preferences with a provided id"),
            @ApiResponse(responseCode = "400", description = """
            Failed to update department preferences for a student with provided ID"). Happens if:"
            - some of the departments with provided IDs do not exist
            - student for provided ID doesn't exist""")})
    @PutMapping("/{id}")
    public StudentDto setStudentDepartmentPreferences(@PathVariable Long id,
                                                      @RequestBody List<Long> departmentIds) {
        Student updated = studentService.updateDepartmentPreferences(id, departmentIds);
        return studentMapper.toDto(updated);
    }

    @Operation(summary = "Remove student", description = "Removes a single student with a unique ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Successful removal of student with a provided ID"),
            @ApiResponse(responseCode = "404", description = "No student found for provided ID")})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteStudentById(@PathVariable Long id) {
        studentService.removeStudentById(id);
    }
}