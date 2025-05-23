package com.lms_backend.lms_project.dto.response;

import com.lms_backend.lms_project.dto.AssignmentDTO;
import com.lms_backend.lms_project.entity.Assignment;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AssignmentResponse extends CommonApiResponse {
    private List<Assignment> assignments = new ArrayList<>();

    private Assignment assignment;

    private List<AssignmentDTO> assignmentDTOS;
}
