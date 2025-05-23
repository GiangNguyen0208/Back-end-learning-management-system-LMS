package com.lms_backend.lms_project.serviceimpl;

import com.lms_backend.lms_project.dao.AssignmentDAO;
import com.lms_backend.lms_project.entity.Assignment;
import com.lms_backend.lms_project.service.AssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AssignmentServiceImpl implements AssignmentService {
    @Autowired
    private AssignmentDAO assignmentDAO;

    @Override
    public Assignment add(Assignment assignment) {
        return assignmentDAO.save(assignment);
    }

    @Override
    public Assignment update(Assignment assignment) {
        return assignmentDAO.save(assignment);
    }

    @Override
    public void delete(int id) {
        assignmentDAO.deleteById(id);
    }
    @Override
    public List<Assignment> getAll() {
        return assignmentDAO.findAll();
    }

    @Override
    public List<Assignment> findAllByCourseID(int courseID) {
        return assignmentDAO.findAllByCourseID(courseID);
    }

    @Override
    public Optional<Assignment> findById(int id) {
        return assignmentDAO.findById(id);
    }
}
