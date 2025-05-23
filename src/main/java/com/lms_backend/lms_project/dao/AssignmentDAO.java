package com.lms_backend.lms_project.dao;

import com.lms_backend.lms_project.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentDAO extends JpaRepository<Assignment, Integer> {
    @Query("SELECT a FROM Assignment a WHERE a.course.id = :courseID")
    List<Assignment> findAllByCourseID(@Param("courseID") int courseID);
}
