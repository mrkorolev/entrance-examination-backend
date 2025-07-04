package com.nksoft.entrance_examination.student.controller;

import com.nksoft.entrance_examination.common.mapper.StudentMapper;
import com.nksoft.entrance_examination.student.dto.LoginDto;
import com.nksoft.entrance_examination.student.model.Student;
import com.nksoft.entrance_examination.student.dto.StudentDto;
import com.nksoft.entrance_examination.student.StudentService;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    @PostMapping("/login")
    public StudentDto login(@RequestBody LoginDto dto) {
        Student loggedIn = service.authenticateStudent(dto.getEmail(), dto.getPassword());
        return mapper.toDtoSanitized(loggedIn);
    }
}
