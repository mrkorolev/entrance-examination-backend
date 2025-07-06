package com.nksoft.entrance_examination.examination.service;

import com.nksoft.entrance_examination.examination.model.Exam;
import com.nksoft.entrance_examination.examination.model.GradeType;
import com.nksoft.entrance_examination.examination.repository.ExamCenterRepository;
import com.nksoft.entrance_examination.examination.repository.ExamRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExamService {
    private final ExamRepository repository;

    @Transactional(readOnly = true)
    public List<Exam> findExams() {
        List<Exam> exams = repository.findAll();
        log.info("Total exams found: {}", exams.size());
        return exams;
    }

    @Transactional(readOnly = true)
    public Exam findExamById(Long id) {
        return getByIdOrThrow(id);
    }

    public Exam registerExam(Exam toRegister) {
        validateGradeType(toRegister.getGradeType());
        validateStartTime(toRegister.getStartTime());
        Exam registered = repository.save(toRegister);
        log.info("Exam registered: [{} - starting at {}]",
                registered.getGradeType(),
                registered.getStartTime());
        return registered;
    }

    @Transactional
    public void removeExamById(Long id) {
        int count = repository.deleteByIdReturningCount(id);
        if (count == 0) {
            throw new EntityNotFoundException("Exam with ID = " + id + " does not exist");
        }
        log.info("Exam with ID = {} removed", id);
    }

    private Exam getByIdOrThrow(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Exam with ID = " + id + " does not exist"));
    }

    private void validateGradeType(GradeType gradeType) {
        if (repository.existsByGradeType(gradeType)) {
            throw new IllegalStateException("Exam with grade type = " + gradeType + " already exists");
        }
    }

    private void validateStartTime(LocalDateTime startTime) {
        LocalDate date = startTime.toLocalDate();
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        if (repository.existsByStartTimeBetween(startOfDay, endOfDay)) {
            throw new IllegalStateException("Exam with startTime = " + startTime + " already exists");
        }
    }
}
