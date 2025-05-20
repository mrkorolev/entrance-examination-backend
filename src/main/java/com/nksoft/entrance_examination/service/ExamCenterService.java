package com.nksoft.entrance_examination.service;

import com.nksoft.entrance_examination.entity.ExamCenter;
import com.nksoft.entrance_examination.repository.ExamCenterRepository;
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
public class ExamCenterService {
    private final ExamCenterRepository exCtrRepository;

    @Transactional(readOnly = true)
    public List<ExamCenter> findCenters() {
        List<ExamCenter> centers = exCtrRepository.findAll();
        log.info("Total exam centers found: {}", centers.size());
        return centers;
    }

    @Transactional(readOnly = true)
    public ExamCenter findCenterById(Long id) {
        return getByIdOrThrow(id);
    }

    public ExamCenter registerCenter(ExamCenter toRegister) {
        validateByName(toRegister.getName());
        ExamCenter registered = exCtrRepository.save(toRegister);
        log.info("Exam center registered: [{} - {}]",
                registered.getId(), registered.getName());
        return registered;
    }

    public void removeCenterById(Long id) {
        exCtrRepository.deleteById(id);
        log.info("Exam center with ID = {} removed", id);
    }

    private ExamCenter getByIdOrThrow(Long id) {
        return exCtrRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Exam center with ID = " + id + " does not exist!"));
    }

    private void validateByName(String name) {
        if (exCtrRepository.existsByName(name)) {
            throw new EntityExistsException("Exam center with name '" + name + "' already exists!");
        }
    }
}
