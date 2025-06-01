package com.nksoft.entrance_examination.service;

import com.nksoft.entrance_examination.entity.Student;
import com.nksoft.entrance_examination.entity.StudentStatus;
import com.nksoft.entrance_examination.file.FileExporter;
import com.nksoft.entrance_examination.repository.DepartmentRepository;
import com.nksoft.entrance_examination.repository.StudentRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;
    private final DepartmentRepository depRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileExporter exporter;

    @Transactional
    public Student authenticateStudent(String email, String password) {
        log.info("Student login: [{}, {}]", email, password);
        Student toLogin = studentRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("Student with email '" + email + "' does not exist"));
        validatePasswordsMatch(email, password, toLogin.getPasswordHash());
        return toLogin;
    }

    @Transactional(readOnly = true)
    public List<Student> findStudents() {
        List<Student> students = studentRepository.findAll();
        log.info("Total students found: {}", students.size());
        return students;
    }

    @Transactional(readOnly = true)
    public Student findStudentByCode(Long code) {
        return getByCodeOrThrow(code);
    }

    public Student registerStudent(Student toRegister) {
        validateCodeIsUnique(toRegister.getStudentCode());
        validateEmailDoesntExist(toRegister.getEmail());
        String encrypted = passwordEncoder.encode(toRegister.getPasswordHash());
        toRegister.setPasswordHash(encrypted);
        toRegister.setStatus(StudentStatus.REGISTERED);

        Student registered = studentRepository.save(toRegister);
        log.info("Student registered: [{} - {} - {}]",
                registered.getStudentCode(),
                registered.getName(),
                registered.getEmail());
        return registered;
    }

    // TODO: finish refactoring parsing logic
    @Transactional
    public void processBatchFile(MultipartFile file, String delimiter, int batchSize) throws IOException {
        validateFileNotEmpty(file);
        log.info("Batch file processing: {}, [size: {} bytes, content type: {}, delimiter: {}]",
                file.getOriginalFilename(), file.getSize(), file.getContentType(), delimiter);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            int lineNumber = 0;
            List<Student> toInsert = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) {
                    throw new IllegalArgumentException("Empty are not allowed in a batch file");
                }
                Student toSave = parseToStudent(line, delimiter);
                validateCodeIsUnique(toSave.getStudentCode());
                toInsert.add(toSave);
                if (toInsert.size() == batchSize) {
                    studentRepository.saveAll(toInsert);
                    toInsert.clear();
                }
            }
            if (!toInsert.isEmpty()) {
                studentRepository.saveAll(toInsert);
            }
            log.info("Batch insert complete: {} new students", lineNumber);
        }
    }

    // TODO: extract to a separate component
    private Student parseToStudent(String line, String delimiter) {
        String[] params = line.split(delimiter);
        if (params.length != 15) {
            throw new IllegalArgumentException("File doesn't follow the expected format: [id, name, grade1, grade2, grade3, preferences(10)]");
        }
        Long code = Long.parseLong(params[0]);
        String name = params[1];
        float grade1 = Float.parseFloat(params[2]);
        float grade2 = Float.parseFloat(params[3]);
        float grade3 = Float.parseFloat(params[4]);

        Long[] preferences = new Long[10];
        for(int i = 5, j = 0; i < 15; i++, j++) {
            preferences[j] = Long.parseLong(params[i]);
        }

        Student toSave = new Student();
        toSave.setStudentCode(code);
        toSave.setStatus(StudentStatus.REGISTERED);
        toSave.setName(name);
        toSave.setEmail(code + "@gmail.com");
        toSave.setPasswordHash(code + "_password");
        toSave.setPreferredDepartmentIds(preferences);
        toSave.setCgpa(0.0F);
        toSave.setGrade1Result(grade1);
        toSave.setGrade2Result(grade2);
        toSave.setGrade3Result(grade3);
        return toSave;
    }

    public Student updateDepartmentPreferences(Long code, List<Long> departmentIds) {
        validateAllDepartmentsExist(departmentIds);
        Student existing = getByCodeOrThrow(code);

        Long[] departmentIdsUpdated = departmentIds.toArray(new Long[0]);
        existing.setPreferredDepartmentIds(departmentIdsUpdated);
        existing.setStatus(StudentStatus.CHOICES_SUBMITTED);
        Student updated = studentRepository.save(existing);

        log.info("Updated department preferences for student [code = {}, name: {}]",
                updated.getStudentCode(), updated.getName());
        return updated;
    }

    @Transactional
    public void removeStudentByCode(Long code) {
        int count = studentRepository.deleteByIdReturningCount(code);
        if (count == 0) {
            throw new EntityNotFoundException("Student with code " + code + " does not exist");
        }
        log.info("Removed student with code = {}", code);
    }

    public ResponseEntity<ByteArrayResource> exportStudentsToCsv() {
        List<Student> students = studentRepository.findAll();
        String delimiter = ",";
        StringBuilder header = new StringBuilder();
        header.append("student_code").append(delimiter)
                .append("name").append(delimiter)
                .append("email").append(delimiter)
                .append("cgpa").append(delimiter)
                .append("grade1_result").append(delimiter)
                .append("grade2_result").append(delimiter)
                .append("grade3_result").append(delimiter)
                .append("placed_department_idx").append(delimiter);

        for (int i = 0; i < 10; i++) {
            header.append("choice_").append(i + 1).append("_dep_id");
            if (i != 9) {
                header.append(delimiter);
            }
        }

        ByteArrayResource resource = exporter.exportToCsv(header.toString(), students, s -> {
            StringBuilder row = new StringBuilder();
            row.append(s.getStudentCode()).append(delimiter)
                    .append(s.getName()).append(delimiter)
                    .append(s.getEmail()).append(delimiter)
                    .append(s.getCgpa()).append(delimiter)
                    .append(s.getGrade1Result()).append(delimiter)
                    .append(s.getGrade2Result()).append(delimiter)
                    .append(s.getGrade3Result()).append(delimiter)
                    .append(s.getPlacedPreferenceIdx() != null ? s.getPlacedPreferenceIdx() : "?").append(delimiter);

            for (int i = 0; i < 10; i++) {
                Long depId = s.getPreferredDepartmentIds()[i];
                row.append(depId == null ? "-" : depId);
                if (i != 9) {
                    row.append(delimiter);
                }
            }
            return row.toString();
        });

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=students.csv")
                .contentLength(resource.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    private Student getByCodeOrThrow(Long code) {
        return studentRepository.findById(code).orElseThrow(
                () -> new EntityNotFoundException("Student with code = " + code + " does not exist"));
    }

    private void validateCodeIsUnique(Long code) {
        if (studentRepository.existsById(code)) {
            throw new EntityExistsException("Student with code = " + code + " already exists");
        }
    }

    private void validateEmailDoesntExist(String email) {
        if (studentRepository.existsByEmail(email)) {
            throw new EntityExistsException("Student with email '" + email + "' already exists");
        }
    }

    private void validateAllDepartmentsExist(List<Long> ids) {
        List<Long> missingDepartmentIds = depRepository.findMissingDepartmentIds(ids);
        if (!missingDepartmentIds.isEmpty()) {
            throw new EntityNotFoundException("Some departments for provided codes do not exist: " + missingDepartmentIds);
        }
    }

    private void validateFileNotEmpty(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Provided file is empty, check file contents and try again");
        }
    }

    private void validatePasswordsMatch(String email, String provided, String actualEncoded) {
        if (!passwordEncoder.matches(provided, actualEncoded)) {
            String msg = String.format("Provided incorrect password [%s], for student with email: %s)", provided, email);
            throw new IllegalArgumentException(msg);
        }
    }
}