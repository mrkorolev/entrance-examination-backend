package com.nksoft.entrance_examination.service;

import com.nksoft.entrance_examination.entity.Department;
import com.nksoft.entrance_examination.entity.Student;
import com.nksoft.entrance_examination.repository.DepartmentRepository;
import com.nksoft.entrance_examination.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
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
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlacementService {

    private final StudentRepository studentRepository;
    private final DepartmentRepository departmentRepository;

    @Transactional
    public ByteArrayResource runPlacement() {
        List<Student> students = studentRepository.findAll();
        List<Department> departmentList = departmentRepository.findAll();

        departmentList.sort(Comparator.comparingLong(Department::getId));

        Map<Long, Department> departmentMap = departmentList.stream()
                .collect(Collectors.toMap(Department::getId, d -> d));

        Map<Long, PriorityQueue<StudentWithScore>> departmentQueues = new HashMap<>();
        for (Department department : departmentList) {
            departmentQueues.put(department.getId(), new PriorityQueue<>(Comparator.comparingDouble(s -> s.score)));
        }

        Queue<Student> processingQueue = new ArrayDeque<>(students);
        while (!processingQueue.isEmpty()) {
            Student student = processingQueue.poll();

            if (student.getDepartmentPreferences() == null || student.getPlacedPreferenceIdx() != null && student.getPlacedPreferenceIdx() >= 0) {
                continue;
            }

            int idx = student.getPlacedPreferenceIdx() == null ? 0 : student.getPlacedPreferenceIdx();

            if (idx >= student.getDepartmentPreferences().length) {
                student.setPlacedPreferenceIdx(-1);
                continue;
            }

            Long deptId = student.getDepartmentPreferences()[idx];
            Department department = departmentMap.get(deptId);

            float grade = switch (department.getPreferredGrade()) {
                case GRADE1 -> student.getGrade1Result();
                case GRADE2 -> student.getGrade2Result();
                case GRADE3 -> student.getGrade3Result();
            };
            double score = student.getCgpa() * 0.4 + grade * 0.6;

            PriorityQueue<StudentWithScore> queue = departmentQueues.get(deptId);
            if (queue.size() < department.getQuota()) {
                queue.add(new StudentWithScore(student, score));
                student.setPlacedPreferenceIdx(idx);
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
        for (Department department : departmentList) {
            reportBuilder.append(department.getId()).append(" ")
                    .append(department.getName()).append(" ")
                    .append(department.getPreferredGrade().ordinal() + 1).append(" ")
                    .append(department.getQuota()).append("\n\n");

            Queue<StudentWithScore> placedStudentsQueue = departmentQueues.get(department.getId());
            List<StudentWithScore> placedStudentsPerDepartment = new ArrayList<>(placedStudentsQueue);
            placedStudentsPerDepartment.sort(Comparator.comparingDouble((StudentWithScore s) -> s.score).reversed());
            for (StudentWithScore sws : placedStudentsPerDepartment) {
                Student s = sws.student;
                System.out.println(sws.student);
                reportBuilder
                        .append(s.getId()).append(" ")
                        .append(s.getName()).append(" ");
                float grade = switch (department.getPreferredGrade()) {
                    case GRADE1 -> s.getGrade1Result();
                    case GRADE2 -> s.getGrade2Result();
                    case GRADE3 -> s.getGrade3Result();
                };

                reportBuilder.append(grade).append(" ")
                        .append(s.getPlacedPreferenceIdx() + 1).append(" ")
                        .append(Arrays.asList(s.getDepartmentPreferences()))
                        .append("\n");
            }
            reportBuilder.append("----------------------------------\n");
        }
        return new ByteArrayResource(reportBuilder.toString().getBytes(StandardCharsets.UTF_8));
    }

    private record StudentWithScore(Student student, double score) { }
}
