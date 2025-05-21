package com.nksoft.entrance_examination.controller;

import com.nksoft.entrance_examination.dto.ExamDto;
import com.nksoft.entrance_examination.entity.Exam;
import com.nksoft.entrance_examination.mapper.ExamMapper;
import com.nksoft.entrance_examination.service.ExamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Get exams", description = "Returns a list of exams")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Successful retrieval of exams"))
    @GetMapping
    public List<ExamDto> getExams() {
        List<Exam> foundExams = examService.findExams();
        return examMapper.toDtoList(foundExams);
    }

    @Operation(summary = "Get exam by ID", description = "Returns a single exam with a unique ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful retrieval of exam with a provided ID"),
            @ApiResponse(responseCode = "404", description = "No exam found for provided ID")})
    @GetMapping("/{id}")
    public ExamDto getExamById(@PathVariable Long id) {
        Exam found = examService.findExamById(id);
        return examMapper.toDto(found);
    }

    @Operation(summary = "Register exam", description = "Registers and returns a new exam")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Successful creation of exam with a provided dto"),
            @ApiResponse(responseCode = "400", description = "Exam to be registered has a time/capacity overlap with another exam at the same exam center"),
            @ApiResponse(responseCode = "404", description = "Exam center for provided ID doesn't exist")})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ExamDto addNewExam(@RequestBody ExamDto dto) {
        Exam toRegister = examMapper.toEntity(dto);
        Exam registered = examService.registerExam(toRegister);
        return examMapper.toDto(registered);
    }

    @Operation(summary = "Remove exam", description = "Removes a single exam with a unique ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Successful removal of exam with a provided ID"),
            @ApiResponse(responseCode = "404", description = "No exam found for provided ID")})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteExamById(@PathVariable Long id) {
        examService.removeExamById(id);
    }
}
