package com.lms_backend.lms_project.dto.request;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class UpdateMentorDetailRequest {
    private String bio;
    private int age;
    private String highestQualification;
    private String profession;
    private double experience;
    private String languageCertificate;
    private String degreeLevel;
    private MultipartFile selectedCertificate;
    private MultipartFile profilePic;
}
