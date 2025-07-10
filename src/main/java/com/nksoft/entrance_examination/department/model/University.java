package com.nksoft.entrance_examination.department.model;

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
@Table(name = "universities")
public class University {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Default for OneToMany relation: FetchType.LAZY
    @OneToMany(
            mappedBy = "university",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Department> departments = new ArrayList<>();

    @Column(length = 64, nullable = false, unique = true)
    private String name;
    @Column(length = 128)
    private String description;
}