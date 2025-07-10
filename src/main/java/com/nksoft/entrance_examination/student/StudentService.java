package com.nksoft.entrance_examination.student;

import com.nksoft.entrance_examination.student.model.StudentStatus;
import com.nksoft.entrance_examination.common.file.FileExporter;
import com.nksoft.entrance_examination.student.model.Student;
import com.nksoft.entrance_examination.department.repository.DepartmentRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
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
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository repository;
    private final DepartmentRepository depRepository;
    private final PasswordEncoder encoder;
    private final FileExporter exporter;

    @Transactional
    public Student authenticateStudent(String email, String password) {
        log.info("Student login: [{}, {}]", email, password);
        Student toLogin = getByEmailOrThrow(email);
        validatePasswordsMatch(email, password, toLogin.getPasswordHash());
        return toLogin;
    }


    @Transactional(readOnly = true)
    public List<Student> findStudentsByCodes(List<Long> ids) {
        List<Student> students = repository.findAllById(ids);
        validateAllStudentsExist(ids, students);
        log.info("Total students found: {}", students.size());
        return repository.findAllById(ids);
    }

    @Transactional(readOnly = true)
    public Page<Student> findStudents(Pageable pageable) {
        Page<Student> page = repository.findAll(pageable);
        log.info("Total students found: {}", page.getTotalElements());
        return page;
    }

    @Transactional(readOnly = true)
    public Student findStudentById(Long id) {
        return getByIdOrThrow(id);
    }

    public Student registerStudent(Student toRegister) {
        validateCodeIsUnique(toRegister.getId());
        validateEmailDoesntExist(toRegister.getEmail());
        String encrypted = encoder.encode(toRegister.getPasswordHash());
        toRegister.setPasswordHash(encrypted);
        toRegister.setStatus(StudentStatus.REGISTERED);

        Student registered = repository.save(toRegister);
        log.info("Student registered: [{} - {} - {}]",
                registered.getId(),
                registered.getName(),
                registered.getEmail());
        return registered;
    }

    public Student updateDepartmentPreferences(Long studentId, List<Long> departmentIds) {
        validateAllDepartmentsExist(departmentIds);
        Student existing = getByIdOrThrow(studentId);
        validateStudentIsExamined(existing);

        Long[] departmentIdsUpdated = departmentIds.toArray(new Long[0]);
        existing.setPreferredDepartmentIds(departmentIdsUpdated);
        existing.setStatus(StudentStatus.CHOICES_SUBMITTED);
        Student updated = repository.save(existing);

        log.info("Updated department preferences for student [id = {}, name: {}]",
                updated.getId(), updated.getName());
        return updated;
    }

    @Transactional
    public void removeStudentByCode(Long code) {
        int count = repository.deleteByIdReturningCount(code);
        if (count == 0) {
            throw new EntityNotFoundException("Student with code " + code + " does not exist");
        }
        log.info("Removed student with code = {}", code);
    }

    // TODO: finish refactoring parsing logic
    @Transactional
    public void processBatchFile(MultipartFile file, String delimiter, int batchSize) throws IOException {
        validateFileNotEmpty(file);
        log.warn("Batch file processing: {}, [size: {} bytes, content type: {}, delimiter: {}]",
                file.getOriginalFilename(), file.getSize(), file.getContentType(), delimiter);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            int lineNumber = 0;
            List<Student> toInsert = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) {
                    throw new IllegalArgumentException("Empty lines are not allowed in a batch file");
                }
                Student toSave = parseToStudent(line, delimiter);
                validateCodeIsUnique(toSave.getId());
                toInsert.add(toSave);
                if (toInsert.size() == batchSize) {
                    repository.saveAll(toInsert);
                    toInsert.clear();
                }
            }
            if (!toInsert.isEmpty()) {
                repository.saveAll(toInsert);
            }
            log.warn("Batch insert complete: {} new students", lineNumber);
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
        toSave.setId(code);
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

    public ResponseEntity<ByteArrayResource> exportStudentsToCsv() {
        List<Student> students = repository.findAll();
        String delimiter = ",";
        StringBuilder header = new StringBuilder();
        header.append("Student ID").append(delimiter)
                .append("Name").append(delimiter)
                .append("Email").append(delimiter)
                .append("CGPA").append(delimiter)
                .append("Result for Grade 1").append(delimiter)
                .append("Result for Grade 2").append(delimiter)
                .append("Result for Grade 3").append(delimiter)
                .append("Placed Department Index").append(delimiter);

        for (int i = 0; i < 10; i++) {
            header.append("Dep. choice #").append(i + 1);
            if (i != 9) {
                header.append(delimiter);
            }
        }

        ByteArrayResource resource = exporter.exportToCsv(header.toString(), students, s -> {
            StringBuilder row = new StringBuilder();
            row.append(s.getId()).append(delimiter)
                    .append(s.getName()).append(delimiter)
                    .append(s.getEmail()).append(delimiter)
                    .append(s.getCgpa()).append(delimiter)
                    .append(s.getGrade1Result() != null ? s.getGrade1Result() : "-").append(delimiter)
                    .append(s.getGrade2Result() != null ? s.getGrade1Result() : "-").append(delimiter)
                    .append(s.getGrade3Result() != null ? s.getGrade3Result() : "-").append(delimiter)
                    .append(s.getPlacedPreferenceIdx() != null ? s.getPlacedPreferenceIdx() : "-").append(delimiter);

            if (s.getPreferredDepartmentIds() == null) {
                for (int i = 0; i < 10; i++) {
                    row.append("-");
                    if (i != 9) {
                        row.append(delimiter);
                    }
                }
            } else {
                for (int i = 0; i < 10; i++) {
                    if (i < s.getPreferredDepartmentIds().length) {
                        row.append(s.getPreferredDepartmentIds()[i]);
                    } else {
                     row.append("-");
                    }

                    if (i != 9) {
                        row.append(delimiter);
                    }
                }
            }

            return row.toString();
        });

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=students.csv")
                .header(HttpHeaders.CONTENT_TYPE, "text/csv")
                .contentLength(resource.contentLength())
                .body(resource);
    }

    private Student getByIdOrThrow(Long code) {
        return repository.findById(code).orElseThrow(
                () -> new EntityNotFoundException("Student with code = " + code + " does not exist"));
    }

    private Student getByEmailOrThrow(String email) {
        return repository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("Student with email '" + email + "' does not exist"));
    }

    private void validateAllStudentsExist(List<Long> ids, List<Student> students) {
        List<Long> missingIds = students.stream()
                .map(Student::getId)
                .filter(id -> !ids.contains(id))
                .toList();
        if (!missingIds.isEmpty()) {
            throw new EntityNotFoundException("Some students for provided ids do not exist: " + missingIds);
        }
    }

    private void validateStudentIsExamined(Student existing) {
        if (existing.getStatus() != StudentStatus.EXAMINED) {
            throw new IllegalStateException("Can't submit department choices for an unexamined student");
        }
    }

    private void validateCodeIsUnique(Long code) {
        if (repository.existsById(code)) {
            throw new EntityExistsException("Student with code = " + code + " already exists");
        }
    }

    private void validateEmailDoesntExist(String email) {
        if (repository.existsByEmail(email)) {
            throw new EntityExistsException("Student with email '" + email + "' already exists");
        }
    }

    private void validateAllDepartmentsExist(List<Long> ids) {
        if (!depRepository.existsByIdIn(ids)) {
            throw new EntityNotFoundException("Some departments for provided codes do not exist!");
        }
    }

    private void validateFileNotEmpty(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Provided file is empty, check file contents and try again");
        }
    }

    private void validatePasswordsMatch(String email, String provided, String actualEncoded) {
        if (!encoder.matches(provided, actualEncoded)) {
            String msg = String.format("Provided incorrect password [%s], for student with email: %s)", provided, email);
            throw new IllegalArgumentException(msg);
        }
    }
}