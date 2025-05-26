package com.lms_backend.lms_project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentDTO {
    private int id;
    private int courseID;
    private String name;
    private String note;
    private MultipartFile assignmentFile;
    private SubmitAssignmentDTO submission;
    private LocalDateTime createdAt;
}
