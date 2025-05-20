package com.nksoft.entrance_examination.service;

import com.nksoft.entrance_examination.entity.Department;
import com.nksoft.entrance_examination.entity.GradeType;
import com.nksoft.entrance_examination.repository.DepartmentRepository;
import com.nksoft.entrance_examination.repository.UniversityRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Transactional(readOnly = true)
    public List<Department> findDepartments() {
        List<Department> departments = depRepository.findAll();
        log.info("Total departments found: {}", departments.size());
        return depRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Department findDepartmentById(Long id) {
        return getByIdOrThrow(id);
    }

    public Department registerDepartment(Department toRegister) {
        validateUniversityExists(toRegister.getUniversity().getId());
        validateDepartmentNameUnique(toRegister.getName());
        Department registered = depRepository.save(toRegister);

        log.info("Registered department: ID = {}, name = {}",
                registered.getName(),
                registered.getName());
        return registered;
    }

    // TODO: consider @Transactional, finish refactoring parsing logic
    public void processBatchFile(MultipartFile file) {
        validateFileNotEmpty(file);
        log.info("Batch file processing: {}, [size: {} bytes, content type: {}]",
                file.getOriginalFilename(),
                file.getSize(),
                file.getContentType());
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            List<Department> batchToSave = new ArrayList<>();
            String line;
            int lineNumber = 0;
            while((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) continue;
                Department toAdd = parseToEntity(line);
                batchToSave.add(toAdd);
            }

            List<Department> saved = depRepository.saveAll(batchToSave);
            log.info("Batch saved: {} new departments", saved.size());
        } catch(IOException e) {
            throw new RuntimeException("Failed to read batch file, contact IT support");
        }
    }

    private Department parseToEntity(String line) {
        String[] parts = line.split("\\s+");
        Long departmentId = Long.parseLong(parts[0]);
        String departmentName = parts[1];
        int ordinal = Integer.parseInt(parts[2]) - 1;
        GradeType gradeType = GradeType.values()[ordinal];
        int capacity = Integer.parseInt(parts[3]);

        return new Department(departmentId, null, gradeType, departmentName, capacity);
    }

    public void removeDepartmentById(Long id) {
        depRepository.deleteById(id);
        log.info("Removed department with ID = {}", id);
    }

    private Department getByIdOrThrow(Long id) {
        return depRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Department with ID = " + id + " does not exist"));
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
            throw new IllegalArgumentException("Provided file is empty, check file content or contact IT support");
        }
    }
}
