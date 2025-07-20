package com.nksoft.entrance_examination.placement;

import com.nksoft.entrance_examination.common.aspect.ProfileExecution;
import com.nksoft.entrance_examination.common.config.props.NormalizationProperties;
import com.nksoft.entrance_examination.department.model.Department;
import com.nksoft.entrance_examination.examination.model.Exam;
import com.nksoft.entrance_examination.examination.model.ExamResult;
import com.nksoft.entrance_examination.examination.model.GradeType;
import com.nksoft.entrance_examination.examination.repository.ExamRepository;
import com.nksoft.entrance_examination.examination.repository.ExamResultRepository;
import com.nksoft.entrance_examination.student.model.Student;
import com.nksoft.entrance_examination.student.model.StudentStatus;
import com.nksoft.entrance_examination.department.repository.DepartmentRepository;
import com.nksoft.entrance_examination.student.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlacementService {
    private final DepartmentRepository departmentRepository;
    private final StudentRepository studentRepository;
    private final ExamResultRepository examResultRepository;
    private final ExamRepository examRepository;
    private final PlacementResultRepository repository;
    private final NormalizationProperties normalProps;

    @Transactional(readOnly = true)
    public List<PlacementResult> findPlacementsForDepartment(Long departmentId) {
        validateDepartmentExists(departmentId);
        List<PlacementResult> placements = repository.findByDepartment_IdOrderByFinalScoreDesc(departmentId);
        log.info("Found {} placements [departmentId: {}]", placements.size(), departmentId);
        return placements;
    }

    @ProfileExecution(logMemory = true)
    @Transactional
    public void runPlacementAlgorithm() {
        validatePlacementsToBeRun();
        // TODO: testing flag
//        Map<Exam, List<ExamResult>> resultsMap = examResultRepository.findAll().stream()
//                .collect(Collectors.groupingBy(ExamResult::getExam));
//        validateAllExamsConducted(resultsMap.keySet());
//        calculateStatisticalParamsForExams(resultsMap);

        List<Student> students = studentRepository.findByStatus(StudentStatus.CHOICES_SUBMITTED);
        List<Department> departmentList = departmentRepository.findAll();
        Map<Long, Department> departmentMap = departmentList.stream()
                .collect(Collectors.toMap(Department::getId, Function.identity()));

        Map<Long, PriorityQueue<StudentWithScore>> departmentHeaps = new HashMap<>();
        for (Department department : departmentList) {
            departmentHeaps.put(
                    department.getId(),
                    new PriorityQueue<>(Comparator.comparingDouble(s -> s.score)));
        }

        Queue<Student> processingQueue = new ArrayDeque<>(students);
        while (!processingQueue.isEmpty()) {
            Student student = processingQueue.poll();
            processCurrentStudent(student, departmentMap, departmentHeaps, processingQueue);
        }
        studentRepository.saveAll(students);
        log.debug("Placement completed for {} students", students.size());

        int departmentsBufferSize = 5;
        int bufferCounter = 0;
        List<PlacementResult> placements = new ArrayList<>();
        persistPlacementResultsWithBuffer(departmentList, departmentHeaps, placements, bufferCounter, departmentsBufferSize);
        log.debug("Placement results persisted successfully");
    }

    private void processCurrentStudent(Student student, Map<Long, Department> departmentMap, Map<Long, PriorityQueue<StudentWithScore>> departmentQueues, Queue<Student> processingQueue) {
        if (student.getPlacedPreferenceIdx() == null) {
            student.setPlacedPreferenceIdx(0);
        }
        if (student.getPlacedPreferenceIdx() == student.getPreferredDepartmentIds().length) {
            student.setStatus(StudentStatus.REJECTED);
            student.setPlacedPreferenceIdx(-1);
            return;
        }

        int idx = student.getPlacedPreferenceIdx();
        Long deptId = student.getPreferredDepartmentIds()[idx];
        Department department = departmentMap.get(deptId);

        float gradeResult = getPreferredGradeResult(department, student);
        double score = student.getCgpa() * 0.4 + gradeResult * 0.6;
        PriorityQueue<StudentWithScore> queue = departmentQueues.get(deptId);
        if (queue.size() < department.getQuota()) {
            queue.add(new StudentWithScore(student, score));
            student.setPlacedPreferenceIdx(idx);
            student.setStatus(StudentStatus.PLACED);
        } else if (queue.peek().score < score) {
            StudentWithScore removed = queue.poll();
            removed.student.setPlacedPreferenceIdx(removed.student.getPlacedPreferenceIdx() + 1);
            processingQueue.add(removed.student);
            queue.add(new StudentWithScore(student, score));
            student.setPlacedPreferenceIdx(idx);
        } else {
            student.setPlacedPreferenceIdx(idx + 1);
            processingQueue.add(student);
        }
    }

    private void persistPlacementResultsWithBuffer(List<Department> departmentList,
                                                   Map<Long, PriorityQueue<StudentWithScore>> departmentQueues,
                                                   List<PlacementResult> placements,
                                                   int bufferCounter,
                                                   int departmentsBufferSize) {
        for (Department d : departmentList) {
            Queue<StudentWithScore> placedStudentsQueue = departmentQueues.get(d.getId());
            for (StudentWithScore sws : placedStudentsQueue) {
                Student s = sws.student;
                float gradeResult = getPreferredGradeResult(d, s);

                PlacementResult result = new PlacementResult();
                result.setStudent(s);
                result.setDepartment(d);
                result.setGrade(d.getPreferredGrade());
                result.setFinalScore(gradeResult);
                placements.add(result);
                bufferCounter++;
            }
            if (bufferCounter == departmentsBufferSize) {
                repository.saveAll(placements);
                placements.clear();
                bufferCounter = 0;
            }
        }
        if (!placements.isEmpty()) {
            repository.saveAll(placements);
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<ByteArrayResource> exportPlacementsToTxt() {
        List<Department> departments = departmentRepository.findAllByOrderById();
        Map<Long, List<PlacementResult>> placementsByDepartment = repository
                .rankedByFinalScoreForDepartmentStream()
                .collect(Collectors.groupingBy(pr -> pr.getDepartment().getId()));

        StringBuilder placementResults = new StringBuilder();
        appendPlacedStudentsForDepartments(departments, placementResults, placementsByDepartment);
        appendRejectedStudents(placementResults);
        ByteArrayResource resource = new ByteArrayResource(
                placementResults.toString().getBytes(StandardCharsets.UTF_8)
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=placements.txt")
                .header(HttpHeaders.CONTENT_TYPE, "text/csv")
                .contentLength(resource.contentLength())
                .body(resource);
    }

    private void appendPlacedStudentsForDepartments(List<Department> departments, StringBuilder placementResults, Map<Long, List<PlacementResult>> placementsByDepartment) {
        for (Department d : departments) {
            placementResults.append(d.getId()).append(" ")
                    .append(d.getName()).append(" ")
                    .append(d.getPreferredGrade().ordinal() + 1).append(" ")
                    .append(d.getQuota()).append("\n\n");

            for (PlacementResult p : placementsByDepartment.get(d.getId())) {
                Student s = p.getStudent();
                placementResults.append(s.getId()).append(" ")
                        .append(s.getName()).append(" ")
                        .append(s.getGrade1Result()).append(" ")
                        .append(s.getGrade2Result()).append(" ")
                        .append(s.getGrade3Result()).append(" ")
                        .append(s.getPlacedPreferenceIdx() + 1).append(" ")
                        .append(Arrays.asList(s.getPreferredDepartmentIds())).append("\n");
            }
            placementResults.append("--------------------------------------------------------------------\n");
        }
    }

    private void appendRejectedStudents(StringBuilder placementResults) {
        List<Student> rejectedStudents = studentRepository.findByStatusOrderByNameAsc(StudentStatus.REJECTED);
        placementResults.append(-1).append(" ")
                .append("DIDNT GET PLACED").append("\n\n");
        for (Student s : rejectedStudents) {
            placementResults.append(s.getId()).append(" ")
                    .append(s.getName()).append(" ")
                    .append(s.getGrade1Result()).append(" ")
                    .append(s.getGrade2Result()).append(" ")
                    .append(s.getGrade3Result()).append(" ")
                    .append(s.getPlacedPreferenceIdx()).append(" ")
                    .append(Arrays.asList(s.getPreferredDepartmentIds()))
                    .append("\n");
        }
        log.debug("Appended {} rejected students", rejectedStudents.size());
    }

    private void calculateStatisticalParamsForExams(Map<Exam, List<ExamResult>> resultsMap) {
        for (Exam e : resultsMap.keySet()) {
            List<ExamResult> results = resultsMap.get(e);
            float mean = calculateMeanForResults(results);
            float sd = calculateSdForResults(results, mean);
            normalizeResults(results, mean, sd);
            e.setMean(mean);
            e.setStandardDeviation(sd);
            examResultRepository.saveAll(results);
        }
        examRepository.saveAll(resultsMap.keySet());
    }

    private void normalizeResults(List<ExamResult> results, float mean, float sd) {
        results.forEach(r -> {
            float rawScore = r.getRawScore();
            float rescaledMean = normalProps.getRescaledMean();
            float rescaledSd = normalProps.getRescaledSd();
            float normalized = rescaledMean + (rawScore - mean) * (rescaledSd / sd);
            r.setNormalizedScore(normalized);
        });
    }

    private float calculateMeanForResults(List<ExamResult> examResults) {
        return (float)examResults.stream()
                .mapToDouble(ExamResult::getRawScore)
                .average()
                .orElseThrow(() -> new IllegalStateException("Could not calculate mean for results"));
    }

    private float calculateSdForResults(List<ExamResult> examResults, float mean) {
        double variance = examResults.stream()
                .mapToDouble(er -> Math.pow(er.getRawScore() - mean, 2))
                .average()
                .orElseThrow(() -> new IllegalStateException("Could not calculate standard deviation for results"));
        return (float)Math.sqrt(variance);
    }

    private void validatePlacementsToBeRun() {
        if (repository.count() != 0) {
            throw new IllegalStateException("Placement algorithm has already been run recently");
        }
    }

    private void validateAllExamsConducted(Collection<Exam> exams) {
        if (exams.size() != GradeType.values().length) {
            throw new IllegalStateException("Not all examinations have been conducted by this date");
        }
    }

    private float getPreferredGradeResult(Department department, Student student) {
        return switch (department.getPreferredGrade()) {
            case GRADE1 -> student.getGrade1Result();
            case GRADE2 -> student.getGrade2Result();
            case GRADE3 -> student.getGrade3Result();
        };
    }

    private void validateDepartmentExists(Long departmentId) {
        if (!departmentRepository.existsById(departmentId)) {
            throw new EntityNotFoundException("Department with id " + departmentId + " not found");
        }
    }

    private record StudentWithScore(Student student, double score) {}
}
