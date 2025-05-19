package com.nksoft.entrance_examination.service;

import com.nksoft.entrance_examination.dto.UniversityDto;
import com.nksoft.entrance_examination.entity.University;
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
public class UniversityService {
    private final UniversityRepository uniRepository;

    @Transactional(readOnly = true)
    public List<University> getAllUniversities() {
        return uniRepository.findAll();
    }

    @Transactional(readOnly = true)
    public University getUniversityById(Long id) {
        return getByIdOrThrow(id);
    }

    public University registerUniversity(University toRegister) {
        validateByName(toRegister.getName());
        University registered = uniRepository.save(toRegister);
        log.info("Uni registered: [{} - {}]",
                registered.getId(), registered.getName());
        return registered;
    }

    private void validateByName(String universityName) {
        if (uniRepository.existsByName(universityName)) {
            throw new IllegalStateException("University with name " + universityName + " already exists");
        }
    }

    public void removeUniversityById(Long id) {
        uniRepository.deleteById(id);
    }

    private University getByIdOrThrow(Long id) {
        return uniRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("University with ID = " + id + " does not exist!"));
    }
}
