package com.lms_backend.lms_project.serviceimpl;

import com.lms_backend.lms_project.Utility.Constant;
import com.lms_backend.lms_project.dao.AssignmentDAO;
import com.lms_backend.lms_project.dao.AssignmentSubmissionDAO;
import com.lms_backend.lms_project.dto.SubmitAssignmentDTO;
import com.lms_backend.lms_project.entity.Assignment;
import com.lms_backend.lms_project.entity.AssignmentSubmission;
import com.lms_backend.lms_project.entity.User;
import com.lms_backend.lms_project.service.AssignmentSubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AssignmentSubmissionServiceImpl implements AssignmentSubmissionService {

    @Autowired
    private AssignmentSubmissionDAO assignmentSubmissionDAO;

    @Override
    public AssignmentSubmission submitAssignment(User student, Assignment assignment, String submissionFile) {
        AssignmentSubmission submission = new AssignmentSubmission();
        submission.setStudent(student);
        submission.setAssignment(assignment);
        submission.setSubmissionFile(submissionFile);
        submission.setCreateAt(LocalDateTime.now());
        submission.setStatus(Constant.GradingType.PENDING.value()); // Chờ chấm
        return assignmentSubmissionDAO.save(submission);
    }

    @Override
    public List<AssignmentSubmission> getPendingSubmissionsByAssignment(int assignmentId) {
        return assignmentSubmissionDAO.findAllByAssignmentIdAndStatus(assignmentId, Constant.GradingType.PENDING.value());
    }

    @Override
    public AssignmentSubmission gradeSubmission(int submissionId, SubmitAssignmentDTO request) {
        Optional<AssignmentSubmission> optional = assignmentSubmissionDAO.findById(submissionId);
        if (optional.isEmpty()) {
            throw new RuntimeException("Submission not found");
        }
        AssignmentSubmission submission = optional.get();
        submission.setScore(request.getScore());
        submission.setFeedback(request.getFeedback());
        submission.setStatus(Constant.GradingType.GRADED.value());
        submission.setUpdateAt(LocalDateTime.now());
        return assignmentSubmissionDAO.save(submission);
    }

    @Override
    public List<AssignmentSubmission> getSubmissionsByStudent(User student) {
        return assignmentSubmissionDAO.findByStudentAndStatus(student, Constant.GradingType.PENDING.value());
    }

    @Override
    public AssignmentSubmission save(AssignmentSubmission submission) {
        return assignmentSubmissionDAO.save(submission);
    }

    @Override
    public Optional<AssignmentSubmission> findByStudentAndAssignment(User student, Assignment assignment) {
        return assignmentSubmissionDAO.findByStudentAndAssignment(student, assignment);
    }

    @Override
    public List<AssignmentSubmission> getGradedSubmissionsByAssignment(int assignmentId) {
        return assignmentSubmissionDAO.findAllByAssignmentIdAndStatus(assignmentId, Constant.GradingType.GRADED.value());
    }

    @Override
    public List<AssignmentSubmission> getGradedSubmissionsByStudentID(int studentId, String status) {
        return assignmentSubmissionDAO.findAllSubmissionsByGradedAndStudentID(studentId, status);
    }
}
