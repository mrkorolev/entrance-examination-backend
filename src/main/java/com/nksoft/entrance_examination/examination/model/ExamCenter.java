package com.nksoft.entrance_examination.examination.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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

    @OneToMany(
            mappedBy = "examCenter",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<ExamEntry> examEntries = new ArrayList<>();
    // TODO: check LAZY

    @Column(length = 64, nullable = false, unique = true)
    private String name;
    @Column(length = 128, nullable = false)
    private String address;

    @Column(name = "total_rooms", nullable = false)
    private int totalRooms;
    @Column(name = "room_capacity", nullable = false)
    private int roomCapacity;
}
