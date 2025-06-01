package com.lms_backend.lms_project.service;

import com.lms_backend.lms_project.dto.SubmitAssignmentDTO;
import com.lms_backend.lms_project.entity.Assignment;
import com.lms_backend.lms_project.entity.AssignmentSubmission;
import com.lms_backend.lms_project.entity.User;

import java.util.List;
import java.util.Optional;

public interface AssignmentSubmissionService {
    AssignmentSubmission submitAssignment(User student, Assignment assignment, String submissionFile);
    List<AssignmentSubmission> getPendingSubmissionsByAssignment(int assignmentId);
    AssignmentSubmission gradeSubmission(int submissionId, SubmitAssignmentDTO request);
    List<AssignmentSubmission> getSubmissionsByStudent(User student);

    AssignmentSubmission save(AssignmentSubmission submission);

    Optional<AssignmentSubmission> findByStudentAndAssignment(User student, Assignment assignment);
    List<AssignmentSubmission> getGradedSubmissionsByAssignment(int assignmentId, int studentId);

    List<AssignmentSubmission> getGradedSubmissionsByStudentID(int studentId, String status);
}
