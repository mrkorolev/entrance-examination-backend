package com.nksoft.entrance_examination.examination.service;

import com.nksoft.entrance_examination.common.file.FileExporter;
import com.nksoft.entrance_examination.examination.model.BookletType;
import com.nksoft.entrance_examination.examination.model.Exam;
import com.nksoft.entrance_examination.examination.model.ExamEntry;
import com.nksoft.entrance_examination.examination.model.ExamResult;
import com.nksoft.entrance_examination.examination.model.GradeType;
import com.nksoft.entrance_examination.examination.repository.ExamEntryRepository;
import com.nksoft.entrance_examination.examination.repository.ExamRepository;
import com.nksoft.entrance_examination.examination.repository.ExamResultRepository;
import com.nksoft.entrance_examination.student.StudentRepository;
import com.nksoft.entrance_examination.student.model.Student;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExamResultService {
    private final ExamResultRepository repository;
    private final ExamRepository examRepository;
    private final StudentRepository studentRepository;
    private final FileExporter exporter;
    private final ExamEntryRepository entryRepository;

    @Transactional(readOnly = true)
    public Page<ExamResult> findResults(
            @PageableDefault(size = 20, sort = "id") Pageable pageable)
    {
        Page<ExamResult> page = repository.findAll(pageable);
        log.info("Total exam results found: {}", page.getTotalElements());
        return page;
    }

    @Transactional
    public void processBatchFile(GradeType examGradeType,
                                 MultipartFile file,
                                 String delimiter,
                                 int batchSize) throws IOException {
        validateFileNotEmpty(file);
        log.info("Batch file processing: {}, [size: {} bytes, content type: {}, delimiter: {}]",
                file.getOriginalFilename(), file.getSize(), file.getContentType(), delimiter);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            int lineNumber = 0;
            int lastLength = -1;
            Map<BookletType, String> bookletKeys = new HashMap<>();
            for (BookletType bt : BookletType.values()) {
                lineNumber++;
                String currentLine = reader.readLine().toUpperCase();
                validateBookletKeys(delimiter, lastLength, currentLine, lineNumber);

                bookletKeys.put(bt, currentLine);
                lastLength = currentLine.length();
            }

            List<ExamResult> results = new ArrayList<>();
            Exam exam = getExamByGradeTypeOrThrow(examGradeType);
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) {
                    throw new IllegalArgumentException("Empty lines are not allowed in a batch file");
                }
                ExamResult toSave = parseToResult(line, delimiter);
                toSave.setExam(exam);
                evaluateResult(lineNumber, toSave, bookletKeys);
                results.add(toSave);
                if (results.size() == batchSize) {
                    repository.saveAll(results);
                    results.clear();
                }
            }
            if (!results.isEmpty()) {
                repository.saveAll(results);
            }

            for (BookletType bt : bookletKeys.keySet()) {
                String keys = bookletKeys.get(bt);
                switch (bt) {
                    case A:
                        exam.setBookletAKeys(keys);
                        break;
                    case B:
                        exam.setBookletBKeys(keys);
                        break;
                    case C:
                        exam.setBookletCKeys(keys);
                        break;
                }
            }

            log.info("Batch insert complete (exam type - {}): {} new exam results",
                    exam.getGradeType(),
                    lineNumber);
        }
    }

    private ExamResult parseToResult(String line, String delimiter) {
        String[] params = line.split(delimiter);
        if (params.length != 3) {
            throw new IllegalArgumentException("File doesn't follow the expected format: [raw_answers, booklet_type, student_code]");
        }

        String rawAnswers = params[0].toUpperCase();
        BookletType bt = BookletType.valueOf(params[1].toUpperCase());
        Long studentCode = Long.parseLong(params[2]);
        validateStudentExists(studentCode);
        ExamEntry entry = getEntryByStudentCodeOrElseThrow(studentCode);

        ExamResult result = new ExamResult();
        result.setExamEntry(entry);
        result.setRawAnswers(rawAnswers);
        result.setBookletType(bt);
        return result;
    }

    private void evaluateResult(int lineNumber,
                                ExamResult result,
                                Map<BookletType, String> bookletAnswers) {
        String answerKeys = bookletAnswers.get(result.getBookletType());
        String studentKeys = result.getRawAnswers();
        if (answerKeys.length() != studentKeys.length()) {
            throw new IllegalArgumentException("Mismatched lengths: answer key and student answers [line ]" + lineNumber);
        }

        int correct = 0;
        int incorrect = 0;
        int blank = 0;
        for (int i = 0; i < studentKeys.length(); i++) {
            char studentAnswer = studentKeys.charAt(i);
            char correctAnswer = answerKeys.charAt(i);

            if (studentAnswer == '*') {
                blank++;
            } else if (studentAnswer == correctAnswer) {
                correct++;
            } else {
                incorrect++;
            }
        }

        float score = (float) (correct - (incorrect / 4.0));
        result.setCorrect(correct);
        result.setIncorrect(incorrect);
        result.setUnanswered(blank);
        result.setRawScore(score < 0 ? 0 : score);
    }

    public ResponseEntity<ByteArrayResource> exportResultsToCsv() {
        List<ExamResult> results = repository.findAll();
        String delimiter = ",";
        StringBuilder header = new StringBuilder();
        header.append("Student Code").append(delimiter)
                .append("Name").append(delimiter)
                .append("Email").append(delimiter)
                .append("Exam Grade Type").append(delimiter)
                .append("Booklet Type").append(delimiter)
                .append("Student Answers").append(delimiter)
                .append("Correct #").append(delimiter)
                .append("Incorrect #").append(delimiter)
                .append("Unanswered #").append(delimiter)
                .append("Raw Score").append(delimiter)
                .append("Normalized Score");

        ByteArrayResource resource = exporter.exportToCsv(header.toString(), results, r -> {
            StringBuilder row = new StringBuilder();
            Student s = r.getExamEntry().getStudent();
            row.append(s.getId()).append(delimiter)
                    .append(s.getName()).append(delimiter)
                    .append(s.getEmail()).append(delimiter)
                    .append(r.getExam().getGradeType()).append(delimiter)
                    .append(r.getBookletType()).append(delimiter)
                    .append(r.getRawAnswers()).append(delimiter)
                    .append(r.getCorrect()).append(delimiter)
                    .append(r.getIncorrect()).append(delimiter)
                    .append(r.getUnanswered()).append(delimiter)
                    .append(r.getRawScore()).append(delimiter)
                    .append(r.getNormalizedScore());

            return row.toString();
        });

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=exam_results.csv")
                .header(HttpHeaders.CONTENT_TYPE, "text/csv")
                .contentLength(resource.contentLength())
                .body(resource);
    }

    private ExamEntry getEntryByStudentCodeOrElseThrow(Long studentCode) {
        return entryRepository.findByStudent_Id(studentCode)
                .orElseThrow(() -> new IllegalArgumentException("No entry found for student code: " + studentCode));
    }

    private void validateFileNotEmpty(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Provided file is empty, check file contents and try again");
        }
    }

    private Exam getExamByGradeTypeOrThrow(GradeType gradeType) {
        return examRepository.findByGradeType(gradeType).orElseThrow(
                () -> new IllegalStateException("Exam with grade type = " + gradeType + " already exists"));
    }

    private void validateStudentExists(Long studentCode) {
        if (!studentRepository.existsById(studentCode)) {
            throw new IllegalStateException("Student with code " + studentCode + " does not exist");
        }
    }

    private void validateBookletKeys(String delimiter, int lastLength, String currentLine, int lineNumber) {
        if (lastLength != -1 && (currentLine.length() != lastLength || currentLine.contains(delimiter))) {
            String msg = String.format("Booklet keys contains delimiter or are not the same length [line %d]",
                    lineNumber);
            throw new IllegalArgumentException(msg);
        }
    }
}
