package com.lms_backend.lms_project.dto.response;

import com.lms_backend.lms_project.dto.AssignmentDTO;
import com.lms_backend.lms_project.dto.SubmitAssignmentDTO;
import com.lms_backend.lms_project.entity.Assignment;
import com.lms_backend.lms_project.entity.AssignmentSubmission;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SubmitAssignmentResponse extends CommonApiResponse {
    private List<AssignmentSubmission> submissions = new ArrayList<>();

    private AssignmentSubmission assignmentSubmission;

    private List<SubmitAssignmentDTO> submitAssignmentDTOS;
}
