package com.lms_backend.lms_project.dao;

import com.lms_backend.lms_project.dto.response.RatingResponse;
import com.lms_backend.lms_project.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RatingDAO extends JpaRepository<Rating, Long> {

    @Query("SELECT NEW com.lms_backend.dto.RatingListResponse(" +
            "r, u.id, u.firstName, u.lastName, u.avatar, u.role) " +
            "FROM CourseRating r JOIN r.user u WHERE r.course.id = :courseId " +
            "ORDER BY r.createdAt DESC")
    List<RatingResponse> findByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT AVG(r.rating) FROM CourseRating r WHERE r.course.id = :courseId")
    Double calculateAverageRating(@Param("courseId") Long courseId);

    @Query("SELECT COUNT(r) FROM CourseRating r WHERE r.course.id = :courseId")
    Long countByCourseId(@Param("courseId") Long courseId);

    boolean existsByUserIdAndCourseId(Long userId, Long courseId);
}
