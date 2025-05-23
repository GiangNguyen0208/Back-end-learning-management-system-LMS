package com.lms_backend.lms_project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentDTO {
    private int courseID;
    private String name;

    private String note;

    private MultipartFile assignmentFile;
}
