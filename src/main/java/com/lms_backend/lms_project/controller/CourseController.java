package com.lms_backend.lms_project.controller;


import com.lms_backend.lms_project.dto.request.AddCourseRequestDto;
import com.lms_backend.lms_project.dto.request.AddCourseSectionRequestDto;
import com.lms_backend.lms_project.dto.request.AddCourseSectionTopicRequest;
import com.lms_backend.lms_project.dto.response.CourseResponseDto;
import com.lms_backend.lms_project.resource.CourseResource;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/course")
@CrossOrigin(origins = "http://localhost:5173")
public class CourseController {
    @Autowired
    private CourseResource courseResource;

    @PostMapping("add")
    @Operation(summary = "Api to add the Mentor Course")
    public ResponseEntity<CourseResponseDto> addCourse(AddCourseRequestDto request) {
        return this.courseResource.addCourse(request);
    }

    @PostMapping("section/add")
    @Operation(summary = "Api to add the course section")
    public ResponseEntity<CourseResponseDto> addCourseSection(@RequestBody AddCourseSectionRequestDto request) {
        return this.courseResource.addCourseSection(request);
    }

    @PostMapping("section/topic/add")
    @Operation(summary = "Api to add the course section topic")
    public ResponseEntity<CourseResponseDto> addCourseSectionTopic(AddCourseSectionTopicRequest request) {
        return this.courseResource.addCourseSectionTopic(request);
    }
}
