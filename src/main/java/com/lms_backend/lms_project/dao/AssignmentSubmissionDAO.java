package com.lms_backend.lms_project.dao;

import com.lms_backend.lms_project.entity.Assignment;
import com.lms_backend.lms_project.entity.AssignmentSubmission;
import com.lms_backend.lms_project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentSubmissionDAO extends JpaRepository<AssignmentSubmission, Integer> {
    // Lấy danh sách bài nộp chờ chấm của 1 bài tập
    @Query("SELECT s FROM AssignmentSubmission s WHERE s.assignment.id = :assignmentId AND s.student.id = :studentId AND s.status = :status")
    List<AssignmentSubmission> findAllByAssignmentIdAndStudenIdAndStatus(@Param("assignmentId") int assignmentId, @Param("studentId") int studentId, @Param("status") String status);

    @Query("SELECT s FROM AssignmentSubmission s WHERE s.assignment.id = :assignmentId AND s.status = :status")
    List<AssignmentSubmission> findAllByAssignmentIdAndStatus(@Param("assignmentId") int assignmentId, @Param("status") String status);


    // Lấy danh sách bài nộp chờ chấm của 1 student
    List<AssignmentSubmission> findByStudentAndStatus(User student, String status);

    // Lấy danh sách bài nộp chờ chấm theo nhiều bài tập (ví dụ theo list bài tập của mentor)
    List<AssignmentSubmission> findByAssignmentInAndStatus(List<Assignment> assignments, String status);

    @Query("SELECT s FROM AssignmentSubmission s WHERE s.student = :student AND s.assignment = :assignment and s.status = 'Pending'")
    Optional<AssignmentSubmission> findByStudentAndAssignment(@Param("student") User student, @Param("assignment") Assignment assignment);

    @Query("SELECT s FROM AssignmentSubmission s WHERE s.student.id = :studentId and s.status = :status")
    List<AssignmentSubmission> findAllSubmissionsByGradedAndStudentID(@Param("studentId") int studentId, @Param("status") String status);
}
