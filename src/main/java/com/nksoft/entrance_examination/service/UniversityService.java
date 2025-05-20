package com.nksoft.entrance_examination.service;

import com.nksoft.entrance_examination.entity.University;
import com.nksoft.entrance_examination.repository.UniversityRepository;
import jakarta.persistence.EntityExistsException;
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
    public List<University> findUniversities() {
        List<University> universities = uniRepository.findAll();
        log.info("Total universities found: {}", universities.size());
        return universities;
    }

    @Transactional(readOnly = true)
    public University findUniversityById(Long id) {
        return getByIdOrThrow(id);
    }

    public University registerUniversity(University toRegister) {
        validateByName(toRegister.getName());
        University registered = uniRepository.save(toRegister);
        log.info("Uni registered: [{} - {}]",
                registered.getId(), registered.getName());
        return registered;
    }

    public void removeUniversityById(Long id) {
        uniRepository.deleteById(id);
        log.info("University with ID = {} removed", id);
    }

    private University getByIdOrThrow(Long id) {
        return uniRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("University with ID = " + id + " does not exist!"));
    }

    private void validateByName(String universityName) {
        if (uniRepository.existsByName(universityName)) {
            throw new EntityExistsException("University with name " + universityName + " already exists");
        }
    }
}
