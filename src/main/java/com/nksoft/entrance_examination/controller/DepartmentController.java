package com.nksoft.entrance_examination.controller;

import com.nksoft.entrance_examination.dto.DepartmentDto;
import com.nksoft.entrance_examination.entity.Department;
import com.nksoft.entrance_examination.mapper.DepartmentMapper;
import com.nksoft.entrance_examination.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/departments")
public class DepartmentController {
    private final DepartmentService depService;
    private final DepartmentMapper depMapper;

    @GetMapping
    public List<DepartmentDto> getAllDepartments() {
        List<Department> foundDepartments = depService.findAllDepartments();
        return depMapper.toDtoList(foundDepartments);
    }

    @GetMapping("/{id}")
    public DepartmentDto getDepartmentById(@PathVariable Long id) {
        Department found = depService.findDepartmentById(id);
        return depMapper.toDto(found);
    }

    @PostMapping
    public DepartmentDto createDepartment(@RequestBody DepartmentDto dto) {
        Department toRegister = depMapper.toEntity(dto);
        Department registered = depService.registerDepartment(toRegister);
        return depMapper.toDto(registered);
    }

    @DeleteMapping("/{id}")
    public void deleteDepartmentById(@PathVariable Long id) {
        depService.removeDepartmentById(id);
    }
}
