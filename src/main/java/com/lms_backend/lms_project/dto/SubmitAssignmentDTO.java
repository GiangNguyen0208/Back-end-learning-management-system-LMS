package com.lms_backend.lms_project.dto;

import com.lms_backend.lms_project.dto.response.CommonApiResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmitAssignmentDTO {
    int id;
    int studentId;
    int assignmentId;
    MultipartFile submissionFile;
    private String status;
    private Double score;
    private String feedback;
    private LocalDateTime createdAt;
}
