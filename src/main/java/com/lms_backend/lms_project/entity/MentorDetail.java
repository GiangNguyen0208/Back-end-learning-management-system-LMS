package com.lms_backend.lms_project.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class MentorDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(columnDefinition = "LONGTEXT")
    private String bio;

    private int age;

    private String highestQualification; // B Tech, B Pharm

    private String profession;

    private double experience;

    private String languageCertificate;

    private String degreeLevel;

    private String selectedCertificate;

    private String profilePic;

    private int quantityCourse;

    private int quantityStudent;

}
