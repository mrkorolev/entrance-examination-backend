package com.nksoft.entrance_examination.service;

import com.nksoft.entrance_examination.entity.Exam;
import com.nksoft.entrance_examination.repository.ExamCenterRepository;
import com.nksoft.entrance_examination.repository.ExamRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExamService {
    private final ExamRepository examRepository;
    private final ExamCenterRepository exCtrRepository;

    @Transactional(readOnly = true)
    public List<Exam> findExams() {
        List<Exam> exams = examRepository.findAll();
        log.info("Total exams found: {}", exams.size());
        return exams;
    }

    @Transactional(readOnly = true)
    public Exam findExamById(Long id) {
        return getByIdOrThrow(id);
    }

    public Exam registerExam(Exam toRegister) {
        validateExamCenterExists(toRegister.getExamCenter().getId());
        validateExamTimeOverlap(toRegister);
        Exam registered = examRepository.save(toRegister);
        log.info("Exam registered: [{} - {}]",
                registered.getId(),
                registered.getTargetGrade().getGradeName());
        return registered;
    }

    public void removeExamById(Long id) {
        examRepository.deleteById(id);
        log.info("Exam with ID = {} removed", id);
    }

    private Exam getByIdOrThrow(Long id) {
        return examRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("University with ID = " + id + " does not exist!"));
    }

    private void validateExamCenterExists(Long id) {
        if (!exCtrRepository.existsById(id)) {
            throw new EntityNotFoundException("Exam center with ID = " + id + " does not exist!");
        }
    }

    // TODO: consider capacity & date_time based validation
    private void validateExamTimeOverlap(Exam toRegister) {
        // to follow...
    }
}
