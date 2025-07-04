package com.nksoft.entrance_examination.examination.controller;

import com.nksoft.entrance_examination.examination.dto.ExamCenterDto;
import com.nksoft.entrance_examination.examination.model.ExamCenter;
import com.nksoft.entrance_examination.common.mapper.ExamCenterMapper;
import com.nksoft.entrance_examination.examination.service.ExamCenterService;
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
@RequestMapping("/exam-centers")
@Tag(name = "Exam Center", description = "Operations related to exam center management")
public class ExamCenterController {
    private final ExamCenterService exCtrService;
    private final ExamCenterMapper exCtrMapper;

    // TODO: add pagination, request param for available centers
    @Operation(summary = "Get exam centers", description = "Returns a list of exam centers")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Successful retrieval of exam centers"))
    @GetMapping
    public List<ExamCenterDto> getCenters() {
        List<ExamCenter> foundCenters = exCtrService.findCenters();
        return exCtrMapper.toDtoList(foundCenters);
    }

    @Operation(summary = "Get exam center by ID", description = "Returns a single exam center with a unique ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful retrieval of exam center with a provided ID"),
            @ApiResponse(responseCode = "404", description = "No exam center found for provided ID")})
    @GetMapping("/{id}")
    public ExamCenterDto getCenterById(@PathVariable Long id) {
        ExamCenter found = exCtrService.findCenterById(id);
        return exCtrMapper.toDto(found);
    }

    @Operation(summary = "Register exam center", description = "Registers and returns a newly created exam center")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Successful creation of exam center with a provided dto"),
            @ApiResponse(responseCode = "400", description = "Exam center with provided name already exists")})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ExamCenterDto addNewCenter(@RequestBody ExamCenterDto dto) {
        ExamCenter toRegister = exCtrMapper.toEntity(dto);
        ExamCenter registered = exCtrService.registerCenter(toRegister);
        return exCtrMapper.toDto(registered);
    }

    @Operation(summary = "Remove exam center", description = "Removes a single exam center with a unique ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Successful removal of exam center with a provided ID"),
            @ApiResponse(responseCode = "404", description = "No exam center found for provided ID")})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteCenterById(@PathVariable Long id) {
        exCtrService.removeCenterById(id);
    }
}
