package com.nksoft.entrance_examination.controller;

import com.nksoft.entrance_examination.dto.UniversityDto;
import com.nksoft.entrance_examination.entity.University;
import com.nksoft.entrance_examination.mapper.UniversityMapper;
import com.nksoft.entrance_examination.service.UniversityService;
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
@RequestMapping("/universities")
public class UniversityController {
    private final UniversityService uniService;
    private final UniversityMapper uniMapper;

    @GetMapping
    public List<UniversityDto> findAllUniversities() {
        List<University> foundUniversities = uniService.getAllUniversities();
        return uniMapper.toDtoList(foundUniversities);
    }

    @GetMapping("/{id}")
    public UniversityDto findUniversityById(@PathVariable Long id) {
        University found = uniService.getUniversityById(id);
        return uniMapper.toDto(found);
    }

    @PostMapping
    public UniversityDto addNewUniversity(@RequestBody UniversityDto dto) {
        University toRegister = uniMapper.toEntity(dto);
        University registered = uniService.registerUniversity(toRegister);
        return uniMapper.toDto(registered);
    }

    @DeleteMapping("/{id}")
    public void removeUniversityById(@PathVariable Long id) {
        uniService.removeUniversityById(id);
    }
}
