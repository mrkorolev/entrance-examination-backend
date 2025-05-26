package com.nksoft.entrance_examination.service;

import com.nksoft.entrance_examination.entity.Department;
import com.nksoft.entrance_examination.entity.GradeType;
import com.nksoft.entrance_examination.file.FileExporter;
import com.nksoft.entrance_examination.repository.DepartmentRepository;
import com.nksoft.entrance_examination.repository.UniversityRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
public class DepartmentService {
    private final DepartmentRepository depRepository;
    private final UniversityRepository uniRepository;
    private final FileExporter exporter;

    @Transactional(readOnly = true)
    public List<Department> findDepartments() {
        List<Department> departments = depRepository.findAll();
        log.info("Total departments found: {}", departments.size());
        return depRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Department findDepartmentByCode(Long code) {
        return getByCodeOrThrow(code);
    }

    public Department registerDepartment(Department toRegister) {
        validateCodeIsUnique(toRegister.getDepartmentCode());
        validateDepartmentNameUnique(toRegister.getName());
        validateUniversityExists(toRegister.getUniversity().getId());

        Department registered = depRepository.save(toRegister);

        log.info("Registered department: code = {}, name = {}",
                registered.getDepartmentCode(),
                registered.getName());
        return registered;
    }

    // TODO: finish refactoring parsing logic
    @Transactional
    public void processBatchFile(MultipartFile file, String delimiter, int batchSize) throws IOException {
        validateFileNotEmpty(file);
        log.info("Batch file processing: {}, [size: {} bytes, content type: {}]",
                file.getOriginalFilename(),
                file.getSize(),
                file.getContentType());
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            List<Department> toInsert = new ArrayList<>();
            String line;
            int lineNumber = 0;
            while((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) {
                    throw new IllegalArgumentException("Empty are not allowed in a batch file");
                };
                Department toSave = parseToDepartment(line, delimiter);
                validateCodeIsUnique(toSave.getDepartmentCode());
                toInsert.add(toSave);
                if (toInsert.size() == batchSize) {
                    depRepository.saveAll(toInsert);
                    toInsert.clear();
                }
            }
            if (!toInsert.isEmpty()) {
                depRepository.saveAll(toInsert);
            }
            log.info("Batch insert complete: {} new departments", lineNumber);
        }
    }

    private Department parseToDepartment(String line, String delimiter) {
        String[] parts = line.split(delimiter);
        Long code = Long.parseLong(parts[0]);
        String departmentName = parts[1];
        int ordinal = Integer.parseInt(parts[2]) - 1;
        GradeType gradeType = GradeType.values()[ordinal];
        int quota = Integer.parseInt(parts[3]);

        Department department = new Department();
        department.setDepartmentCode(code);
        department.setName(departmentName);
        department.setPreferredGrade(gradeType);
        department.setQuota(quota);
        return department;
    }

    @Transactional
    public void removeDepartmentById(Long code) {
        int count = depRepository.deleteByIdReturningCount(code);
        if (count == 0) {
            throw new EntityNotFoundException("Department with code = " + code + " does not exist");
        }
        log.info("Removed department with code = {}", code);
    }

    private Department getByCodeOrThrow(Long code) {
        return depRepository.findById(code).orElseThrow(
                () -> new EntityNotFoundException("Department with code = " + code + " does not exist"));
    }

    private void validateCodeIsUnique(Long departmentCode) {
        if (depRepository.existsById(departmentCode)) {
            throw new EntityExistsException("Department with code = " + departmentCode + " already exists");
        }
    }

    private void validateUniversityExists(Long universityId) {
        if (!uniRepository.existsById(universityId)) {
            throw new EntityNotFoundException("University with ID = " + universityId + " does not exist");
        }
    }

    private void validateDepartmentNameUnique(String departmentName) {
        if (depRepository.existsByName(departmentName)) {
            throw new IllegalStateException("Department with name = " + departmentName + " already exists");
        }
    }

    private void validateFileNotEmpty(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Provided file is empty, check file contents and try again");
        }
    }

    public ResponseEntity<ByteArrayResource> exportDepartmentsToCsv() {
        List<Department> departments = depRepository.findAll();
        String delimiter = ",";
        StringBuilder header = new StringBuilder();
        header.append("dep_code").append(delimiter)
                .append("dep_name").append(delimiter)
                .append("pref_grade_type").append(delimiter)
                .append("quota");

        ByteArrayResource resource = exporter.exportToCsv(header.toString(), departments, d -> {
            StringBuilder row = new StringBuilder();
            return row.append(d.getDepartmentCode()).append(delimiter)
                    .append(d.getName()).append(delimiter)
                    .append(d.getPreferredGrade().ordinal() + 1).append(delimiter)
                    .append(d.getQuota())
                    .toString();
        });
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=departments.csv")
                .contentLength(resource.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
