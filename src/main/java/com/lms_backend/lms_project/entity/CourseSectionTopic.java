package com.lms_backend.lms_project.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class CourseSectionTopic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String srNo;

    private String name;

    private String description;

    private String videoFileName;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "section_id")
    private CourseSection courseSection;

}
