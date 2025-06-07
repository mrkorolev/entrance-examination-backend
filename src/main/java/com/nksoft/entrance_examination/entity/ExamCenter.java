package com.nksoft.entrance_examination.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "exam_centers")
public class ExamCenter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // save/remove exam in case of ExamCenter save/removal
    // remove exams from the exam table if exam center is removed
    @OneToMany(
            mappedBy = "examCenter",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Exam> exams = new ArrayList<>();

    @Column(length = 64, unique = true, nullable = false)
    private String name;
    @Column(nullable = false, length = 128)
    private String address;
    @Column(name = "total_rooms", nullable = false)
    private int totalRooms;
    @Column(name = "room_capacity", nullable = false)
    private int roomCapacity;
}
