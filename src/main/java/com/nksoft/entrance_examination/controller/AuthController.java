package com.nksoft.entrance_examination.controller;

import com.nksoft.entrance_examination.dto.LoginDto;
import com.nksoft.entrance_examination.dto.StudentDto;
import com.nksoft.entrance_examination.entity.Student;
import com.nksoft.entrance_examination.mapper.StudentMapper;
import com.nksoft.entrance_examination.service.StudentService;
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
public class AuthController {
    private final StudentService studentService;
    private final StudentMapper studentMapper;

    @PostMapping("/login")
    public StudentDto login(@RequestBody LoginDto dto) {
        log.info("Request login dto: {}", dto);
        Student toLogin = studentService.findStudentByEmail(dto.getEmail(), dto.getPassword());
        return studentMapper.toDto(toLogin);
    }
}
