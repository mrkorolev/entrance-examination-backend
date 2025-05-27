package com.nksoft.entrance_examination.controller;

import com.nksoft.entrance_examination.dto.LoginDto;
import com.nksoft.entrance_examination.dto.StudentDto;
import com.nksoft.entrance_examination.entity.Student;
import com.nksoft.entrance_examination.mapper.StudentMapper;
import com.nksoft.entrance_examination.service.StudentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Operations related to student authentication")
public class AuthController {
    private final StudentService studentService;
    private final StudentMapper studentMapper;

    @PostMapping("/login")
    public StudentDto login(@RequestBody LoginDto dto) {
        Student loggedIn = studentService.authenticateStudent(dto.getEmail(), dto.getPassword());
        return studentMapper.toDtoSanitized(loggedIn);
    }
}
