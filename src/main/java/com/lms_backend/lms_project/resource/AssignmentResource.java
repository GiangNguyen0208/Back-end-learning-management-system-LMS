package com.lms_backend.lms_project.resource;

import com.lms_backend.lms_project.dto.AssignmentDTO;
import com.lms_backend.lms_project.dto.CourseDTO;
import com.lms_backend.lms_project.dto.response.AssignmentResponse;
import com.lms_backend.lms_project.dto.response.CommonApiResponse;
import com.lms_backend.lms_project.dto.response.CourseResponseDto;
import com.lms_backend.lms_project.entity.Assignment;
import com.lms_backend.lms_project.entity.Category;
import com.lms_backend.lms_project.entity.Course;
import com.lms_backend.lms_project.entity.User;
import com.lms_backend.lms_project.service.AssignmentService;
import com.lms_backend.lms_project.service.CourseService;
import com.lms_backend.lms_project.service.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Component
public class AssignmentResource {
    private final Logger LOG = LoggerFactory.getLogger(CourseResource.class);

    @Autowired
    private CourseService courseService;

    @Autowired
    private StorageService storageService;

    @Autowired
    private AssignmentService assignmentService;

    public ResponseEntity<AssignmentResponse> addAssignment(AssignmentDTO request) {
        LOG.info("received request for adding assignment");

        AssignmentResponse response = new AssignmentResponse();

        if (request == null) {
            response.setResponseMessage("missing request body");
            response.setSuccess(false);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        if (request.getName() == null || request.getNote() == null || request.getAssignmentFile() == null || request.getCourseID() == 0) {
            response.setResponseMessage("missing input");
            response.setSuccess(false);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        Course course = courseService.getById(request.getCourseID());

        if (course == null) {
            response.setResponseMessage("course not found");
            response.setSuccess(false);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        String assignmentFile = this.storageService.storeAssignment(request.getAssignmentFile());

        Assignment assignment = Assignment.builder()
                .name(request.getName())
                .note(request.getNote())
                .assignmentFile(assignmentFile)
                .createdAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .course(course)    // *** set course ở đây ***
                .build();

        // Thêm assignment vào danh sách assignments của course
        course.getAssignments().add(assignment);

        // Lưu course kèm theo assignment (nếu cascade đúng)
        courseService.add(course);

        response.setAssignments(course.getAssignments());
        response.setResponseMessage("Assignment is updated");
        response.setSuccess(true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<CommonApiResponse> updateAssignment(int id, AssignmentDTO request) {
        CommonApiResponse response = new CommonApiResponse();

        if (id == 0) {
            response.setResponseMessage("missing id assignment");
            response.setSuccess(false);
            return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
        }

        if (request == null) {
            response.setResponseMessage("missing request body");
            response.setSuccess(false);
            return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
        }

        Optional<Assignment> optionalAssignment = assignmentService.findById(id);
        if (optionalAssignment.isEmpty()) {
            response.setResponseMessage("Assignment not found");
            response.setSuccess(false);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        Assignment assignmentUpdate = optionalAssignment.get();

        assignmentUpdate.setName(request.getName());
        assignmentUpdate.setNote(request.getNote());
        assignmentUpdate.setUpdateAt(LocalDateTime.now());
        if (request.getAssignmentFile() != null) {
            String assignmentFile = this.storageService.storeAssignment(request.getAssignmentFile());
            assignmentUpdate.setAssignmentFile(assignmentFile);
        }

        assignmentService.update(assignmentUpdate);

        response.setResponseMessage("Assignment is updated");
        response.setSuccess(true);
        return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);
    }

    public ResponseEntity<CommonApiResponse> deleteAssignment(int assignmentId) {
        CommonApiResponse response = new CommonApiResponse();

        if (assignmentId == 0) {
            response.setResponseMessage("missing id assignment");
            response.setSuccess(false);
            return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
        }
        Optional<Assignment> optionalAssignment = assignmentService.findById(assignmentId);
        if (optionalAssignment.isEmpty()) {
            response.setResponseMessage("Assignment not found");
            response.setSuccess(false);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        assignmentService.delete(assignmentId);

        response.setResponseMessage("Assignment is deleted");
        response.setSuccess(true);
        return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);
    }
}
