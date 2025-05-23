package com.lms_backend.lms_project.service;

import com.lms_backend.lms_project.entity.Assignment;
import com.lms_backend.lms_project.entity.AssignmentSubmission;
import com.lms_backend.lms_project.entity.User;

import java.util.List;
import java.util.Optional;

public interface AssignmentSubmissionService {
    AssignmentSubmission submitAssignment(User student, Assignment assignment, String submissionFile);
    List<AssignmentSubmission> getPendingSubmissionsByAssignment(Assignment assignment);
    AssignmentSubmission gradeSubmission(int submissionId, Double score, String feedback);
    List<AssignmentSubmission> getSubmissionsByStudent(User student);

    AssignmentSubmission save(AssignmentSubmission submission);

    Optional<AssignmentSubmission> findByStudentAndAssignment(User student, Assignment assignment);
}
