package com.nksoft.entrance_examination.service;

import com.nksoft.entrance_examination.entity.Department;
import com.nksoft.entrance_examination.repository.DepartmentRepository;
import com.nksoft.entrance_examination.repository.UniversityRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentRepository depRepository;
    private final UniversityRepository universityRepository;

    @Transactional(readOnly = true)
    public List<Department> findAllDepartments() {
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

    public void removeDepartmentById(Long id) {
        depRepository.deleteById(id);
        log.info("Removed department with ID = {}", id);
    }

    private Department getByIdOrThrow(Long id) {
        return depRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Department with ID = " + id + " does not exist"));
    }

    private void validateUniversityExists(Long universityId) {
        if (!universityRepository.existsById(universityId)) {
            throw new EntityNotFoundException("University with ID = " + universityId + " does not exist");
        }
    }

    private void validateDepartmentNameUnique(String departmentName) {
        if (depRepository.existsByName(departmentName)) {
            throw new IllegalStateException("Department with name = " + departmentName + " already exists");
        }
    }
}
