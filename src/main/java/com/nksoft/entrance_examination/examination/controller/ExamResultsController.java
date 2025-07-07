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
    // TODO: finish controller, only GET and POST requests allowed!
    /*
        API: Import, Export, Get (by EntryId)
     */
    private final ExamResultService service;
    private final ExamResultMapper mapper;

    @GetMapping
    public List<ExamResultDto> getResults() {
        List<ExamResult> results = service.findResults();
        return mapper.toDtoList(results);
    }

    @Operation(summary = "Exam result file insert", description = "Inserts exam results as well as answer keys for particular exam type in a batch insert file")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Successful insertion of exam results from a batch insert file"),
            @ApiResponse(responseCode = "400", description = """
            An error occurred while parsing through the batch file. Happens if:
            - provided student file is empty"
            - error happened while parsing the file (e.g. file format/delimiter is invalid)"""),
            @ApiResponse(responseCode = "500", description = "I/O error while opening/closing the file")})
    @PostMapping("/import")
    public ResponseEntity<?> uploadStudentsBatch(@RequestBody MultipartFile file,
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
    public ResponseEntity<ByteArrayResource> exportStudents() {
        return service.exportResultsToCsv();
    }
}
