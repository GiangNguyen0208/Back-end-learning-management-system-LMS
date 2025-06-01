package com.lms_backend.lms_project.controller;

import com.lms_backend.lms_project.dto.SubmitAssignmentDTO;
import com.lms_backend.lms_project.dto.response.CommonApiResponse;
import com.lms_backend.lms_project.dto.response.SubmitAssignmentResponse;
import com.lms_backend.lms_project.entity.Assignment;
import com.lms_backend.lms_project.entity.AssignmentSubmission;
import com.lms_backend.lms_project.entity.User;
import com.lms_backend.lms_project.resource.AssignmentSubmissionResource;
import com.lms_backend.lms_project.service.AssignmentSubmissionService;
import com.lms_backend.lms_project.service.AssignmentService;
import com.lms_backend.lms_project.service.UserService;
import com.lowagie.text.DocumentException;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/assignment-submissions")
@RequiredArgsConstructor
public class AssignmentSubmissionController {

    @Autowired
    private AssignmentSubmissionService submissionService;

    @Autowired
    private AssignmentSubmissionResource assignmentSubmissionResource;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private UserService userService;

    // Student nộp bài (POST /api/assignment-submissions/submit)
    @PostMapping("/submit")
    public ResponseEntity<SubmitAssignmentResponse> submitAssignment(@ModelAttribute SubmitAssignmentDTO request) {
        return assignmentSubmissionResource.submitAssignment(request);
    }

    // Mentor lấy danh sách bài chờ chấm theo assignmentId (GET /api/assignment-submissions/pending?assignmentId=xxx)
    @GetMapping("/pending")
    public ResponseEntity<List<AssignmentSubmission>> getPendingSubmissions(@RequestParam("assignmentId") int assignmentId) {
        List<AssignmentSubmission> submissions = submissionService.getPendingSubmissionsByAssignment(assignmentId);
        return ResponseEntity.ok(submissions);
    }

    @GetMapping("/graded/byAssignment")
    public ResponseEntity<List<AssignmentSubmission>> getGradedSubmissions(@RequestParam("assignmentId") int assignmentId, @RequestParam("studentId") int studentId) {
        List<AssignmentSubmission> submissions = submissionService.getGradedSubmissionsByAssignment(assignmentId, studentId);
        return ResponseEntity.ok(submissions);
    }

    // Mentor chấm điểm bài nộp (PUT /api/assignment-submissions/grade/{submissionId})
    @PutMapping("/grade/{submissionId}")
    public ResponseEntity<?> gradeSubmission(
            @PathVariable int submissionId,
            @RequestBody SubmitAssignmentDTO request
    ) {
        AssignmentSubmission gradedSubmission = submissionService.gradeSubmission(submissionId, request);
        return ResponseEntity.ok(gradedSubmission);
    }

    @GetMapping("/graded/byStudent")
    public ResponseEntity<SubmitAssignmentResponse> getSubmissionsGradedByStudent(
            @RequestParam("studentId") int studentId
    ) {
        return assignmentSubmissionResource.getSubmissionsGradedByStudent(studentId);
    }
}

