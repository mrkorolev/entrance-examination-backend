package com.nksoft.entrance_examination.student.controller;

import com.nksoft.entrance_examination.student.StudentMapper;
import com.nksoft.entrance_examination.student.dto.LoginDto;
import com.nksoft.entrance_examination.student.model.Student;
import com.nksoft.entrance_examination.student.dto.StudentDto;
import com.nksoft.entrance_examination.student.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Operations related to student authentication")
public class AuthController {
    private final StudentService service;
    private final StudentMapper mapper;

    @Operation(summary = "Login for student", description = "Attempts to login the current student (for provided dto)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful login for the provided login dto"),
            @ApiResponse(responseCode = "401", description = "Credentials provided in the dto are not valid"),
            @ApiResponse(responseCode = "404", description = "No student found for provided email (in dto)")})
    @PostMapping("/login")
    public StudentDto login(@Valid @RequestBody LoginDto dto) {
        Student loggedIn = service.authenticateStudent(dto.getEmail(), dto.getPassword());
        return mapper.toDtoSanitized(loggedIn);
    }
}
