package com.lms_backend.lms_project.service;

import com.lms_backend.lms_project.entity.Assignment;

import java.util.List;
import java.util.Optional;

public interface AssignmentService {
    Assignment add(Assignment assignment);
    Assignment update(Assignment assignment);
    void delete(int id);
    List<Assignment> getAll();
    List<Assignment> findAllByCourseID(int courseID);
    Optional<Assignment> findById(int id);
}
