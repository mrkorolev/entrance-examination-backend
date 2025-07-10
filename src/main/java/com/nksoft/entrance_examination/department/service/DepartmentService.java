package com.nksoft.entrance_examination.department.service;

import com.nksoft.entrance_examination.department.model.Department;
import com.nksoft.entrance_examination.department.repository.DepartmentRepository;
import com.nksoft.entrance_examination.department.repository.UniversityRepository;
import com.nksoft.entrance_examination.examination.model.GradeType;
import com.nksoft.entrance_examination.common.file.FileExporter;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
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
    private final DepartmentRepository repository;
    private final UniversityRepository uniRepository;
    private final FileExporter exporter;

    @Transactional(readOnly = true)
    public Page<Department> findDepartments(Pageable pageable) {
        Page<Department> page = repository.findAll(pageable);
        log.info("Total departments found: {}", page.getTotalElements());
        return page;
    }

    @Transactional(readOnly = true)
    public List<Department> findDepartmentsByIds(List<Long> codes) {
        validateAllDepartmentsExist(codes);
        return repository.findAllById(codes);
    }

    @Transactional(readOnly = true)
    public Department findDepartmentByCode(Long code) {
        return getByCodeOrThrow(code);
    }

    public Department registerDepartment(Department toRegister) {
        validateDepartmentDoesntExist(toRegister.getId());
        validateDepartmentNameUnique(toRegister.getName());
        validateUniversityExists(toRegister.getUniversity().getId());

        Department registered = repository.save(toRegister);

        log.info("Registered department: code = {}, name = {}",
                registered.getId(),
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
                }
                Department toSave = parseToDepartment(line, delimiter);
                validateDepartmentDoesntExist(toSave.getId());
                toInsert.add(toSave);
                if (toInsert.size() == batchSize) {
                    repository.saveAll(toInsert);
                    toInsert.clear();
                }
            }
            if (!toInsert.isEmpty()) {
                repository.saveAll(toInsert);
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
        department.setId(code);
        department.setName(departmentName);
        department.setPreferredGrade(gradeType);
        department.setQuota(quota);
        return department;
    }

    @Transactional
    public void removeDepartmentById(Long code) {
        int count = repository.deleteByIdReturningCount(code);
        if (count == 0) {
            throw new EntityNotFoundException("Department with code = " + code + " does not exist");
        }
        log.info("Removed department with code = {}", code);
    }

    public ResponseEntity<ByteArrayResource> exportDepartmentsToCsv() {
        List<Department> departments = repository.findAll();
        String delimiter = ",";
        StringBuilder header = new StringBuilder();
        header.append("Department ID").append(delimiter)
                .append("Name").append(delimiter)
                .append("Preferred Grade").append(delimiter)
                .append("Quota");

        ByteArrayResource resource = exporter.exportToCsv(header.toString(), departments, d -> {
            StringBuilder row = new StringBuilder();
            return row.append(d.getId()).append(delimiter)
                    .append(d.getName()).append(delimiter)
                    .append(d.getPreferredGrade().toString()).append(delimiter)
                    .append(d.getQuota())
                    .toString();
        });
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=departments.csv")
                .header(HttpHeaders.CONTENT_TYPE, "text/csv")
                .contentLength(resource.contentLength())
                .body(resource);
    }

    private Department getByCodeOrThrow(Long code) {
        return repository.findById(code).orElseThrow(
                () -> new EntityNotFoundException("Department with code = " + code + " does not exist"));
    }

    private void validateDepartmentDoesntExist(Long departmentCode) {
        if (repository.existsById(departmentCode)) {
            throw new EntityExistsException("Department with id = " + departmentCode + " already exists");
        }
    }

    private void validateAllDepartmentsExist(List<Long> departmentCodes) {
        List<Long> missingDepartmentCodes = repository.findMissingDepartmentIds(departmentCodes);
        if (!missingDepartmentCodes.isEmpty()) {
            throw new EntityNotFoundException("Some departments for provided codes do not exist: " + missingDepartmentCodes);
        }
    }

    private void validateUniversityExists(Long universityId) {
        if (!uniRepository.existsById(universityId)) {
            throw new EntityNotFoundException("University with ID = " + universityId + " does not exist");
        }
    }

    private void validateDepartmentNameUnique(String departmentName) {
        if (repository.existsByName(departmentName)) {
            throw new IllegalStateException("Department with name = " + departmentName + " already exists");
        }
    }

    private void validateFileNotEmpty(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Provided file is empty, check file contents and try again");
        }
    }
}
