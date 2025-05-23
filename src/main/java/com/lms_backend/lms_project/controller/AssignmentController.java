package com.lms_backend.lms_project.controller;


import com.lms_backend.lms_project.dto.AssignmentDTO;
import com.lms_backend.lms_project.dto.CourseDTO;
import com.lms_backend.lms_project.dto.request.AddCourseRequestDto;
import com.lms_backend.lms_project.dto.response.AssignmentResponse;
import com.lms_backend.lms_project.dto.response.CommonApiResponse;
import com.lms_backend.lms_project.dto.response.CourseResponseDto;
import com.lms_backend.lms_project.entity.Assignment;
import com.lms_backend.lms_project.resource.AssignmentResource;
import com.lms_backend.lms_project.resource.CourseResource;
import com.lms_backend.lms_project.service.AssignmentService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/assignment")
public class AssignmentController {
    @Autowired
    private CourseResource courseResource;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private AssignmentResource assignmentResource;

    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Api to add the assignment")
    public ResponseEntity<AssignmentResponse> addAssignment(@ModelAttribute AssignmentDTO request) {
        return this.assignmentResource.addAssignment(request);
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "Api to update assignment")
    public ResponseEntity<CommonApiResponse> updateAssignment(@PathVariable("id") int id, @ModelAttribute AssignmentDTO request) {
        return assignmentResource.updateAssignment(id, request);
    }

    @GetMapping("/fetch-all/{courseId}")
    @Operation(summary = "Api to update assignment")
    public List<Assignment> updateAssignment(@PathVariable("courseId") int courseId) {
        return assignmentService.findAllByCourseID(courseId);
    }
    @DeleteMapping("/{assignmentId}")
    @Operation(summary = "Api to delete assignment")
    public ResponseEntity<CommonApiResponse> deleteAssignment(@PathVariable("assignmentId") int assignmentId) {
        return assignmentResource.deleteAssignment(assignmentId);
    }

}
