package com.lms_backend.lms_project.dto;

import com.lms_backend.lms_project.dto.response.CommonApiResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmitAssignmentDTO {
    int studentId;
    int assignmentId;
    MultipartFile submissionFile;
}
