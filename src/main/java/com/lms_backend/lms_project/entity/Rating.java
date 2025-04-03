package com.lms_backend.lms_project.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@Table(
        name = "course_ratings",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"user_id", "course_id"}
        ),
        indexes = {
                @Index(columnList = "course_id"),
                @Index(columnList = "user_id"),
                @Index(columnList = "created_at")
        }
)
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Min(1) @Max(5)
    @Column(nullable = false)
    private Integer rating;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    public Rating(Integer rating, String comment, User user, Course course) {
        this.rating = rating;
        this.comment = comment;
        this.user = user;
        this.course = course;
    }
}
