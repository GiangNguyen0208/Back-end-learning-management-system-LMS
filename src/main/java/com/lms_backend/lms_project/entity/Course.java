package com.lms_backend.lms_project.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "mentor_id")
    private User mentor; // tour guide id

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "category_id")
    private Category category;

    private String name;

    private String description;

    private String type; // free, paid

    private BigDecimal fee;

    private String addedDateTime;

    private String notesFileName;

    private String thumbnail;

    private String status;

    private int discountInPercent;

    @Column(columnDefinition = "LONGTEXT")
    private String authorCourseNote;

    @Column(columnDefinition = "LONGTEXT")
    private String specialNote;

    @Column(columnDefinition = "LONGTEXT")
    private String prerequisite;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseSection> sections = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Collaborator> collabrators = new ArrayList<>();

}

