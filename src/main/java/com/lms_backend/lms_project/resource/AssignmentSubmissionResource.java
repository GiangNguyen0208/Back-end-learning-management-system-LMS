package com.lms_backend.lms_project.resource;

import com.lms_backend.lms_project.dto.SubmitAssignmentDTO;
import com.lms_backend.lms_project.dto.response.CommonApiResponse;
import com.lms_backend.lms_project.dto.response.SubmitAssignmentResponse;
import com.lms_backend.lms_project.entity.Assignment;
import com.lms_backend.lms_project.entity.AssignmentSubmission;
import com.lms_backend.lms_project.entity.User;
import com.lms_backend.lms_project.service.AssignmentService;
import com.lms_backend.lms_project.service.AssignmentSubmissionService;
import com.lms_backend.lms_project.service.StorageService;
import com.lms_backend.lms_project.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class AssignmentSubmissionResource {
    private final Logger LOG = LoggerFactory.getLogger(CourseResource.class);

    @Autowired
    private AssignmentSubmissionService submissionService;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private UserService userService;

    @Autowired
    private StorageService storageService;

    public ResponseEntity<SubmitAssignmentResponse> submitAssignment(SubmitAssignmentDTO request) {
        LOG.info("Received request for submitting assignment: " + request.toString());

        SubmitAssignmentResponse response = new SubmitAssignmentResponse();

        // Kiểm tra dữ liệu đầu vào
        if (request.getAssignmentId() == 0 || request.getStudentId() == 0 || request.getSubmissionFile() == null) {
            response.setResponseMessage("Missing assignmentId, studentId or submission file");
            response.setSuccess(false);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        User student = userService.getUserById(request.getStudentId());
        if (student == null) {
            response.setResponseMessage("Student not found");
            response.setSuccess(false);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        Optional<Assignment> optionalAssignment = assignmentService.findById(request.getAssignmentId());
        if (optionalAssignment.isEmpty()) {
            response.setResponseMessage("Assignment not found");
            response.setSuccess(false);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        Assignment assignment = optionalAssignment.get();

        // Lưu file nộp bài mới
        String storedFilePath = storageService.storeAssignmentSubmission(request.getSubmissionFile());

        // Kiểm tra xem đã nộp chưa
        Optional<AssignmentSubmission> existingSubmissionOpt =
                submissionService.findByStudentAndAssignment(student, assignment);

        AssignmentSubmission submission;
        if (existingSubmissionOpt.isPresent()) {
            // Cập nhật file nộp mới
            submission = existingSubmissionOpt.get();
            submission.setSubmissionFile(storedFilePath);
            submission.setUpdateAt(LocalDateTime.now()); // hoặc setUpdatedAt nếu có
            submission = submissionService.save(submission);
        } else {
            // Tạo mới submission
            submission = submissionService.submitAssignment(student, assignment, storedFilePath);
            assignment.getSubmissions().add(submission);
        }

        response.setSubmissions(assignment.getSubmissions());
        response.setResponseMessage("Submit assignment successful");
        response.setSuccess(true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
