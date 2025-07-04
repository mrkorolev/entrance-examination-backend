package com.nksoft.entrance_examination.examination.service;

import com.nksoft.entrance_examination.examination.model.ExamEntry;
import com.nksoft.entrance_examination.examination.repository.ExamEntryRepository;
import com.nksoft.entrance_examination.examination.repository.ExamRepository;
import com.nksoft.entrance_examination.student.repository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExamEntryService {
    private final ExamEntryRepository examEntryRepository;
    private final StudentRepository studentRepository;
    private final ExamRepository examRepository;

    @Transactional(readOnly = true)
    public List<ExamEntry> findEntries() {
        List<ExamEntry> entries = examEntryRepository.findAll();
        log.info("Total exam entries: {}", entries.size());
        return entries;
    }

    @Transactional(readOnly = true)
    public ExamEntry findEntryById(Long id) {
        return getByIdOrThrow(id);
    }

    public ExamEntry registerEntry(ExamEntry toRegister) {
        validateStudentExists(toRegister.getStudent().getStudentCode());
        validateExamExists(toRegister.getExam().getId());
        ExamEntry registered = examEntryRepository.save(toRegister);
        log.info("Exam entry registered: [examId = {}, studentId = {}]",
                registered.getExam().getId(),
                registered.getStudent().getStudentCode());
        return registered;
    }

    @Transactional
    public void removeEntryById(Long id) {
        int count = examEntryRepository.deleteByIdReturningCount(id);
        if (count == 0) {
            throw new EntityNotFoundException("Exam entry with ID = " + id + " does not exist");
        }
        log.info("Removed exam entry with ID = {}", id);
    }

    private ExamEntry getByIdOrThrow(Long id) {
        return examEntryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Exam entry with ID = " + id + " does not exist"));
    }

    private void validateStudentExists(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new EntityNotFoundException("Student with code = " + id + " does not exist");
        }
    }

    private void validateExamExists(Long id) {
        if (!examRepository.existsById(id)) {
            throw new EntityNotFoundException("Exam with ID = " + id + " does not exist");
        }
    }
}
