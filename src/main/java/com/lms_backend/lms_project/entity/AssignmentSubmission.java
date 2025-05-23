package com.lms_backend.lms_project.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.minidev.json.annotate.JsonIgnore;

import java.time.LocalDateTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class AssignmentSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // Bài tập mà student nộp
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assignment_id", nullable = false)
    @JsonIgnore
    private Assignment assignment;

    // Student nộp bài
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    @JsonIgnore
    private User student;

    // File nộp hoặc đường dẫn file
    private String submissionFile;

    // Thời gian nộp
    private LocalDateTime createAt;

    // Trạng thái bài nộp (chờ chấm, đã chấm, ...)
    private String status; // VD: "PENDING", "GRADED"

    // Điểm số mentor cho bài này
    private Double score;

    // Ghi chú chấm bài
    @Column(columnDefinition = "LONGTEXT")
    private String feedback;

    private LocalDateTime updateAt;
}
