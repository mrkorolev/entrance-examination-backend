package com.nksoft.entrance_examination.examination.controller;

import com.nksoft.entrance_examination.common.mapper.ExamResultMapper;
import com.nksoft.entrance_examination.examination.dto.ExamResultDto;
import com.nksoft.entrance_examination.examination.model.ExamResult;
import com.nksoft.entrance_examination.examination.model.GradeType;
import com.nksoft.entrance_examination.examination.service.ExamResultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/exam-results")
public class ExamResultsController {
    private final ExamResultService service;
    private final ExamResultMapper mapper;

    @Operation(summary = "Get students", description = "Returns a Page of exam results")
    @ApiResponse(responseCode = "200", description = "Successful retrieval of students")
    @GetMapping
    public Page<ExamResultDto> getResults(
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        Page<ExamResult> page = service.findResults(pageable);
        return page.map(mapper::toDto);
    }

    @Operation(summary = "Exam result batch file insert", description = "Inserts exam results as well as answer keys for particular exam type in a batch insert file")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Successful insertion of exam results from a batch insert file"),
            @ApiResponse(responseCode = "400", description = """
            An error occurred while parsing through the batch file. Happens if:
            - provided results file is empty"
            - error happened while parsing the file (e.g. file format/delimiter is invalid)"""),
            @ApiResponse(responseCode = "500", description = "I/O error while opening/closing the file")})
    @PostMapping("/import")
    public ResponseEntity<?> importResults(@RequestBody MultipartFile file,
                                           @RequestParam(defaultValue = " ") String delimiter,
                                           @RequestParam(defaultValue = "50") int batchSize,
                                           @RequestParam(name = "exam-grade-type") GradeType examGradeType) throws IOException {
        service.processBatchFile(examGradeType, file, delimiter, batchSize);
        return ResponseEntity.ok("Successfully processed exam results file");
    }

    @Operation(summary = "Export exam results", description = "Exports exam results to a csv batch file")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully exported students to a csv batch file")})
    @GetMapping("/export")
    public ResponseEntity<ByteArrayResource> exportResults() {
        return service.exportResultsToCsv();
    }
}
