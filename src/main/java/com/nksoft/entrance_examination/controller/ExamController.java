package com.nksoft.entrance_examination.controller;

import com.nksoft.entrance_examination.dto.ExamDto;
import com.nksoft.entrance_examination.entity.Exam;
import com.nksoft.entrance_examination.mapper.ExamMapper;
import com.nksoft.entrance_examination.service.ExamService;
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
@RequestMapping("/exams")
@Tag(name = "Exam", description = "Operations related to exam management")
public class ExamController {
    private final ExamService examService;
    private final ExamMapper examMapper;

    @GetMapping
    public List<ExamDto> getExams() {
        List<Exam> foundExams = examService.findExams();
        return examMapper.toDtoList(foundExams);
    }

    @GetMapping("/{id}")
    public ExamDto getExamById(@PathVariable Long id) {
        Exam found = examService.findExamById(id);
        return examMapper.toDto(found);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ExamDto addNewExam(@RequestBody ExamDto dto) {
        Exam toRegister = examMapper.toEntity(dto);
        Exam registered = examService.registerExam(toRegister);
        return examMapper.toDto(registered);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteExamById(@PathVariable Long id) {
        examService.removeExamById(id);
    }
}
