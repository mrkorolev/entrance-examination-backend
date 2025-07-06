package com.nksoft.entrance_examination.examination.service;

import com.nksoft.entrance_examination.examination.model.ExamCenter;
import com.nksoft.entrance_examination.examination.model.ExamEntry;
import com.nksoft.entrance_examination.examination.repository.ExamCenterRepository;
import com.nksoft.entrance_examination.examination.repository.ExamEntryRepository;
import com.nksoft.entrance_examination.student.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExamEntryService {
    private final ExamEntryRepository repository;
    private final StudentRepository studentRepository;
    private final ExamCenterRepository centerRepository;
    private final ConcurrentMap<Long, Object> centerLocks = new ConcurrentHashMap<>();

    @Transactional(readOnly = true)
    public Page<ExamEntry> findEntries(Pageable pageable) {
        Page<ExamEntry> page = repository.findAll(pageable);
        log.info("Total exam entries: {}", page.getTotalElements());
        return page;
    }

    @Transactional(readOnly = true)
    public List<ExamEntry> findEntriesByIds(List<Long> examEntryIds) {
        List<ExamEntry> entries = repository.findAllById(examEntryIds);
        log.info("Total exam entries: {}", entries.size());
        return entries;
    }

    @Transactional(readOnly = true)
    public ExamEntry findEntryById(Long id) {
        return getByIdOrThrow(id);
    }

    @Transactional
    public ExamEntry registerEntry(ExamEntry toRegister) {
        validateStudentExists(toRegister.getStudent().getStudentCode());
        validateStudentNotAlreadyRegistered(toRegister.getStudent().getStudentCode());
        Object lock = centerLocks.computeIfAbsent(toRegister.getExamCenter().getId(), id -> new Object());

        synchronized (lock) {
            ExamCenter center = getCenterByIdOrThrow(toRegister.getExamCenter().getId());
            int capacity = center.getTotalRooms() * center.getRoomCapacity();
            int registrations = repository.currentRegistrationsCountForCenter(center.getId());

            validateCapacityNotExceeded(center.getId(), capacity, registrations);
            int seatNumber = registrations + 1;
            toRegister.setSeatNumber(seatNumber);
            ExamEntry registered = repository.save(toRegister);
            log.info("Exam entry registered: [examId = {}, studentId = {}, seatNumber = {}]",
                    registered.getExamCenter().getId(),
                    registered.getStudent().getStudentCode(),
                    registered.getSeatNumber());
            return registered;
        }
    }

    @Transactional
    public void removeEntryById(Long id) {
        int count = repository.deleteByIdReturningCount(id);
        if (count == 0) {
            throw new EntityNotFoundException("Exam entry with ID = " + id + " does not exist");
        }
        log.info("Removed exam entry with ID = {}", id);
    }

    private ExamEntry getByIdOrThrow(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Exam entry with ID = " + id + " does not exist"));
    }

    private ExamCenter getCenterByIdOrThrow(Long centerId) {
        return centerRepository.findById(centerId).orElseThrow(
                () -> new EntityNotFoundException("Exam center with ID = " + centerId + " does not exist"));
    }

    private void validateStudentExists(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new EntityNotFoundException("Student with code = " + id + " does not exist");
        }
    }

    private void validateCapacityNotExceeded(Long id, int capacity, int registrations) {
        if (registrations >= capacity) {
            throw new IllegalStateException("Exam center with ID = " + id + " is no longer available for registrations");
        }
    }

    private void validateStudentNotAlreadyRegistered(Long studentCode) {
        if (repository.existsByStudent_StudentCode(studentCode)) {
            throw new IllegalStateException("Student with code = " + studentCode + " already has an exam entry");
        }
    }
}
