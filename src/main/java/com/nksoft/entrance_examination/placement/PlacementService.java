package com.nksoft.entrance_examination.placement;

import com.nksoft.entrance_examination.common.config.props.NormalizationProperties;
import com.nksoft.entrance_examination.department.model.Department;
import com.nksoft.entrance_examination.examination.model.Exam;
import com.nksoft.entrance_examination.examination.model.ExamResult;
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
        List<PlacementResult> placements = repository.findByDepartment_IdOrderByRankAsc(departmentId);
        log.info("Found {} placements [departmentId: {}]", placements.size(), departmentId);
        return placements;
    }

    @Transactional
    public ResponseEntity<ByteArrayResource> runPlacementAndExport() {
        validatePlacementsToBeRun();
        Map<Exam, List<ExamResult>> resultsMap = examResultRepository.findAll().stream()
                .collect(Collectors.groupingBy(ExamResult::getExam));
        calculateStatisticalParamsForExams(resultsMap);


        List<Student> students = studentRepository.findByStatus(StudentStatus.CHOICES_SUBMITTED);
        List<Department> departmentList = departmentRepository.findAll();

        departmentList.sort(Comparator.comparingLong(Department::getId));
        Map<Long, Department> departmentMap = departmentList.stream()
                .collect(Collectors.toMap(Department::getId, Function.identity()));

        Map<Long, PriorityQueue<StudentWithScore>> departmentQueues = new HashMap<>();
        for (Department department : departmentList) {
            departmentQueues.put(
                    department.getId(),
                    new PriorityQueue<>(Comparator.comparingDouble(s -> s.score))
            );
        }

        Queue<Student> processingQueue = new ArrayDeque<>(students);
        while (!processingQueue.isEmpty()) {
            Student student = processingQueue.poll();
            if (student.getPlacedPreferenceIdx() == null) {
                student.setPlacedPreferenceIdx(0);
            }
            if (student.getPlacedPreferenceIdx() == student.getPreferredDepartmentIds().length) {
                student.setStatus(StudentStatus.REJECTED);
                student.setPlacedPreferenceIdx(-1);
                continue;
            }

            int idx = student.getPlacedPreferenceIdx();

            Long deptId = student.getPreferredDepartmentIds()[idx];
            Department department = departmentMap.get(deptId);

            float gradeResult = switch (department.getPreferredGrade()) {
                case GRADE1 -> student.getGrade1Result();
                case GRADE2 -> student.getGrade2Result();
                case GRADE3 -> student.getGrade3Result();
            };
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

        studentRepository.saveAll(students);
        log.info("Placement completed for {} students", students.size());

        StringBuilder reportBuilder = new StringBuilder();
        for (Department d : departmentList) {
            reportBuilder.append(d.getId()).append(" ")
                    .append(d.getName()).append(" ")
                    .append(d.getPreferredGrade().ordinal() + 1).append(" ")
                    .append(d.getQuota()).append("\n\n");

            Queue<StudentWithScore> placedStudentsQueue = departmentQueues.get(d.getId());
            List<StudentWithScore> placedStudentsPerDepartment = new ArrayList<>(placedStudentsQueue);
            placedStudentsPerDepartment.sort(Comparator.comparingDouble((StudentWithScore s) -> s.score).reversed());

            int rank = 0;
            List<PlacementResult> placements = new ArrayList<>();
            for (StudentWithScore sws : placedStudentsPerDepartment) {
                Student s = sws.student;
                reportBuilder
                        .append(s.getId()).append(" ")
                        .append(s.getName()).append(" ");
                float gradeResult = switch (d.getPreferredGrade()) {
                    case GRADE1 -> s.getGrade1Result();
                    case GRADE2 -> s.getGrade2Result();
                    case GRADE3 -> s.getGrade3Result();
                };

                reportBuilder.append(gradeResult).append(" ")
                        .append(s.getPlacedPreferenceIdx() + 1).append(" ")
                        .append(Arrays.asList(s.getPreferredDepartmentIds()))
                        .append("\n");

                rank++;
                PlacementResult result = new PlacementResult();
                result.setStudent(s);
                result.setDepartment(d);
                result.setGrade(d.getPreferredGrade());
                result.setFinalScore(gradeResult);
                result.setRank(rank);
                placements.add(result);
            }
            repository.saveAll(placements);
            reportBuilder.append("--------------------------------------------------------------------\n");
        }

        // all those who didn't make it
        List<Student> didntGetPlaced = studentRepository.findAllByPlacedPreferenceIdxOrderByName(-1);
        reportBuilder.append(-1).append(" ")
                .append("DIDNT GET PLACED").append("\n\n");
        for (Student s : didntGetPlaced) {
            reportBuilder.append(s.getId()).append(" ")
                    .append(s.getName()).append(" ")
                    .append(s.getGrade1Result()).append(" ")
                    .append(s.getGrade2Result()).append(" ")
                    .append(s.getGrade3Result()).append(" ")
                    .append(s.getPlacedPreferenceIdx()).append(" ")
                    .append(Arrays.asList(s.getPreferredDepartmentIds()))
                    .append("\n");
        }

        ByteArrayResource reportFile = new ByteArrayResource(
                reportBuilder.toString().getBytes(StandardCharsets.UTF_8)
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=placements.txt")
                .header(HttpHeaders.CONTENT_TYPE, "text/csv")
                .contentLength(reportFile.contentLength())
                .body(reportFile);
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
            int rescaledMean = normalProps.getRescaledMean();
            int rescaledSd = normalProps.getRescaledSd();
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

    private void validateDepartmentExists(Long departmentId) {
        if (!departmentRepository.existsById(departmentId)) {
            throw new EntityNotFoundException("Department with id " + departmentId + " not found");
        }
    }

    private record StudentWithScore(Student student, double score) {}
}
