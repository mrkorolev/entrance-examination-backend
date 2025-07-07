package com.nksoft.entrance_examination.examination.service;

import com.nksoft.entrance_examination.common.file.FileExporter;
import com.nksoft.entrance_examination.examination.model.ExamCenter;
import com.nksoft.entrance_examination.examination.repository.ExamCenterRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExamCenterService {
    private final ExamCenterRepository repository;
    private final FileExporter exporter;

    @Transactional(readOnly = true)
    public List<ExamCenter> findCenters(boolean availableOnly) {
        List<ExamCenter> centers = availableOnly ?
                repository.findAvailable() :
                repository.findAll();
        log.info("Total exam centers found: {}", centers.size());
        return centers;
    }

    @Transactional(readOnly = true)
    public ExamCenter findCenterById(Long id) {
        return getByIdOrThrow(id);
    }

    public ExamCenter registerCenter(ExamCenter toRegister) {
        validateByName(toRegister.getName());
        ExamCenter registered = repository.save(toRegister);
        log.info("Exam center registered: [{} - {}]",
                registered.getId(), registered.getName());
        return registered;
    }

    @Transactional
    public void removeCenterById(Long id) {
        int count = repository.deleteByIdReturningCount(id);
        if (count == 0) {
            throw new EntityNotFoundException("Exam center with ID = " + id + " does not exist");
        }
        log.info("Exam center with ID = {} removed", id);
    }

    public ResponseEntity<ByteArrayResource> exportCentersToCsv() {
        List<ExamCenter> centers = repository.findAll();
        String delimiter = ",";
        StringBuilder header = new StringBuilder();
        header.append("Center ID").append(delimiter)
                .append("Center Name").append(delimiter)
                .append("Total Rooms").append(delimiter)
                .append("Room Capacity").append(delimiter)
                .append("Address");

        ByteArrayResource resource = exporter.exportToCsv(header.toString(), centers, c -> {
            StringBuilder row = new StringBuilder();
            row.append(c.getId()).append(delimiter)
                    .append(c.getName()).append(delimiter)
                    .append(c.getTotalRooms()).append(delimiter)
                    .append(c.getRoomCapacity()).append(delimiter)
                    .append(c.getAddress());

            return row.toString();
        });

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=exam_centers.csv")
                .header(HttpHeaders.CONTENT_TYPE, "text/csv")
                .contentLength(resource.contentLength())
                .body(resource);
    }

    private ExamCenter getByIdOrThrow(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Exam center with ID = " + id + " does not exist"));
    }

    private void validateByName(String name) {
        if (repository.existsByName(name)) {
            throw new EntityExistsException("Exam center with name '" + name + "' already exists");
        }
    }
}
