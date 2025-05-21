package com.nksoft.entrance_examination.service;

import com.nksoft.entrance_examination.entity.Student;
import com.nksoft.entrance_examination.repository.DepartmentRepository;
import com.nksoft.entrance_examination.repository.StudentRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Transactional(readOnly = true)
    public List<Student> findStudents() {
        List<Student> students = studentRepository.findAll();
        log.info("Total students found: {}", students.size());
        return students;
    }

    @Transactional(readOnly = true)
    public Student findStudentById(Long id) {
        return getByIdOrThrow(id);
    }

    public Student registerStudent(Student toRegister) {
        validateEmailDoesntExist(toRegister.getEmail());
        String encrypted = passwordEncoder.encode(toRegister.getPassword());
        toRegister.setPassword(encrypted);

        Student registered = studentRepository.save(toRegister);
        log.info("Student registered: [{} - {} - {}]",
                registered.getId(),
                registered.getName(),
                registered.getEmail());
        return registered;
    }

    // TODO: finish refactoring parsing logic
    public void processBatchFile(MultipartFile file) {
        validateFileNotEmpty(file);
        log.info("Batch file processing: {}, [size: {} bytes, content type: {}]",
                file.getOriginalFilename(),
                file.getSize(),
                file.getContentType());

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            List<Student> batchToSave = new ArrayList<>();
            String line;
            int lineNumber = 0, batchSize = 50;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) continue;
                Student toSave = parseToStudent(line);
                batchToSave.add(toSave);
                if (batchToSave.size() == batchSize) {
                    studentRepository.saveAll(batchToSave);
                    batchToSave.clear();
                }
            }
            if (!batchToSave.isEmpty()) {
                studentRepository.saveAll(batchToSave);
            }
            log.info("Bulk save complete: {} new students", lineNumber);
        } catch(IOException e) {
            throw new RuntimeException("Failed to read batch file, contact IT support");
        }
    }

    private Student parseToStudent(String line) {
        // TODO: finish parsing logic here...
        return null;
    }

    public Student updateDepartmentPreferences(Long id, List<Long> departmentIds) {
        validateAllDepartmentsExist(departmentIds);
        Student existing = getByIdOrThrow(id);

        Long[] departmentIdsUpdated = departmentIds.toArray(new Long[0]);
        existing.setDepartmentPreferences(departmentIdsUpdated);
        Student updated = studentRepository.save(existing);

        log.info("Updated department preferences for student [ID = {}, name: {}]",
                updated.getId(), updated.getName());
        return updated;
    }

    public void removeStudentById(Long id) {
        studentRepository.deleteById(id);
        log.info("Removed student with ID = {}", id);
    }

    private Student getByIdOrThrow(Long id) {
        return studentRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Student with ID = " + id + " does not exist"));
    }

    private void validateEmailDoesntExist(String email) {
        if (studentRepository.existsByEmail(email)) {
            throw new EntityExistsException("Student with email '" + email + "' already exists");
        }
    }

    private void validateAllDepartmentsExist(List<Long> ids) {
        List<Long> missingDepartmentIds = depRepository.findMissingDepartmentIds(ids);
        if (!missingDepartmentIds.isEmpty()) {
            throw new EntityNotFoundException("Some departments for provided IDs do not exist: " + missingDepartmentIds);
        }
    }

    private void validateFileNotEmpty(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Provided file is empty, check file contents and try again");
        }
    }
}
