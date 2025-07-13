package com.nksoft.entrance_examination.examination.service;

import com.nksoft.entrance_examination.common.file.FileExporter;
import com.nksoft.entrance_examination.examination.model.ExamCenter;
import com.nksoft.entrance_examination.examination.model.ExamEntry;
import com.nksoft.entrance_examination.examination.repository.ExamCenterRepository;
import com.nksoft.entrance_examination.examination.repository.ExamEntryRepository;
import com.nksoft.entrance_examination.student.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExamEntryService {
    private final ExamEntryRepository repository;
    private final StudentRepository studentRepository;
    private final ExamCenterRepository centerRepository;
    private final FileExporter exporter;

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

    @Transactional(timeout = 15)
    public ExamEntry registerEntry(ExamEntry toRegister) {
        validateStudentExists(toRegister.getStudent().getId());
        validateStudentNotAlreadyRegistered(toRegister.getStudent().getId());

        ExamCenter center = getLockedCenterByIdOrThrow(toRegister.getExamCenter().getId());
        int capacity = center.getTotalRooms() * center.getRoomCapacity();
        int registrations = repository.countByExamCenter_Id(center.getId());
        validateCapacityNotExceeded(center.getId(), capacity, registrations);
        int seatNumber = registrations + 1;
        toRegister.setRegistrationNumber(seatNumber);

        ExamEntry registered = repository.save(toRegister);
        log.info("Exam entry registered: [examId = {}, studentId = {}, seatNumber = {}]",
                registered.getExamCenter().getId(),
                registered.getStudent().getId(),
                registered.getRegistrationNumber());
        return registered;
    }

    @Transactional
    public void removeEntryById(Long id) {
        int count = repository.deleteByIdReturningCount(id);
        if (count == 0) {
            throw new EntityNotFoundException("Exam entry with ID = " + id + " does not exist");
        }
        log.info("Removed exam entry with ID = {}", id);
    }

    public ResponseEntity<ByteArrayResource> exportEntriesToCsv(Long examCenterId) {
        List<ExamEntry> entries = examCenterId != null ?
                repository.findByExamCenter_Id(examCenterId) :
                repository.findAll();
        String delimiter = ",";
        StringBuilder header = new StringBuilder();
        header.append("Entry ID").append(delimiter)
                .append("Student Name").append(delimiter)
                .append("Email").append(delimiter)
                .append("Room #").append(delimiter)
                .append("Seat #").append(delimiter)
                .append("Exam Center Address");

        ByteArrayResource resource = exporter.exportToCsv(header.toString(), entries, ee -> {
            int roomCapacity = ee.getExamCenter().getRoomCapacity();
            int registrationNumber = ee.getRegistrationNumber();
            int room = (registrationNumber - 1) / roomCapacity + 1;
            int seat = (registrationNumber - 1) % roomCapacity + 1;

            StringBuilder row = new StringBuilder();
            row.append(ee.getId()).append(delimiter)
                    .append(ee.getStudent().getName()).append(delimiter)
                    .append(ee.getStudent().getEmail()).append(delimiter)
                    .append(room).append(delimiter)
                    .append(seat).append(delimiter)
                    .append(ee.getExamCenter().getAddress());
            return row.toString();
        });

        String centerQualifier = String.format(examCenterId == null ?
                "" : "_for_center_%d", examCenterId);
        String fileName = String.format("entries%s.csv", centerQualifier);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .header(HttpHeaders.CONTENT_TYPE, "text/csv")
                .contentLength(resource.contentLength())
                .body(resource);
    }

    private ExamEntry getByIdOrThrow(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Exam entry with ID = " + id + " does not exist"));
    }

    private ExamCenter getLockedCenterByIdOrThrow(Long centerId) {
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
        if (repository.existsByStudent_Id(studentCode)) {
            throw new IllegalStateException("Student with code = " + studentCode + " already has an exam entry");
        }
    }
}