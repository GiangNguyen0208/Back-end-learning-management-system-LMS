package com.lms_backend.lms_project.controller;


import com.lms_backend.lms_project.dto.VideoProgressDTO;
import com.lms_backend.lms_project.dto.request.AddCourseRequestDto;
import com.lms_backend.lms_project.dto.request.AddCourseSectionRequestDto;
import com.lms_backend.lms_project.dto.request.AddCourseSectionTopicRequest;
import com.lms_backend.lms_project.dto.request.RatingRequest;
import com.lms_backend.lms_project.dto.response.CourseResponseDto;
import com.lms_backend.lms_project.dto.response.RatingListResponse;
import com.lms_backend.lms_project.dto.response.RatingResponse;
import com.lms_backend.lms_project.resource.CourseResource;
import com.lms_backend.lms_project.service.RatingService;
import com.lms_backend.lms_project.service.VideoProgressService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/course")
@CrossOrigin(origins = "http://localhost:5173")
public class CourseController {

    @Autowired
    private CourseResource courseResource;

    @Autowired
    VideoProgressService videoProgressService;

    @Autowired
    RatingService ratingService;

    @PostMapping("add")
    @Operation(summary = "Api to add the Mentor Course")
    public ResponseEntity<CourseResponseDto> addCourse(@ModelAttribute AddCourseRequestDto request) {
        return this.courseResource.addCourse(request);
    }

    @PostMapping("section/add")
    @Operation(summary = "Api to add the course section")
    public ResponseEntity<CourseResponseDto> addCourseSection(@RequestBody AddCourseSectionRequestDto request) {
        return this.courseResource.addCourseSection(request);
    }

    @PostMapping(value = "section/topic/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Api to add the course section topic")
    public ResponseEntity<CourseResponseDto> addCourseSectionTopic(@ModelAttribute AddCourseSectionTopicRequest request) {
        return this.courseResource.addCourseSectionTopic(request);
    }

    @GetMapping("/fetch/mentor-wise")
    @Operation(summary = "Api to fetch courses by using status")
    public ResponseEntity<CourseResponseDto> fetchCoursesByMentor(@RequestParam("mentorId") Integer mentorId,
                                                                  @RequestParam("status") String status, @RequestParam("videoShow") String videoShow) {
        return courseResource.fetchCoursesByMentor(mentorId, status, videoShow);
    }
    @GetMapping("/fetch/course-id")
    @Operation(summary = "Api to fetch course by using course id")
    public ResponseEntity<CourseResponseDto> fetchCourseById(@RequestParam("courseId") Integer courseId,
                                                             @RequestParam("videoShow") String videoShow) {
        return courseResource.fetchCourseById(courseId, videoShow);
    }

    @GetMapping(value = "/{courseImageName}", produces = "image/*")
    public void fetchCourseImage(@PathVariable("courseImageName") String courseImageName, HttpServletResponse resp) {
        this.courseResource.fetchCourseImage(courseImageName, resp);
    }

    @GetMapping("/fetch/status-wise")
    @Operation(summary = "Api to fetch courses by using status")
    public ResponseEntity<CourseResponseDto> fetchCoursesByStatus(@RequestParam("status") String status,
                                                                  @RequestParam("videoShow") String videoShow) {
        return courseResource.fetchCoursesByStatus(status, videoShow);
    }

    @GetMapping("/ratings/{courseId}")
    public ResponseEntity<RatingListResponse> getRatingsByCourse(
            @PathVariable int courseId) {
        return ResponseEntity.ok(ratingService.fetchRatingsByCourse(courseId));
    }

    @PostMapping("/rating/add")
    public ResponseEntity<RatingResponse> addRating(
            @Valid @RequestBody RatingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ratingService.addRating(request));
    }

    @GetMapping(value = "/video/{courseSectionTopicVideoFileName}", produces = "video/*")
    public void fetchCourseTopicVideo(
            @PathVariable("courseSectionTopicVideoFileName") String courseSectionTopicVideoFileName,
            HttpServletResponse resp) {
        this.courseResource.fetchCourseTopicVideo(courseSectionTopicVideoFileName, resp);
    }

    @PostMapping("/video/progress")
    public ResponseEntity<?> updateProgress(@RequestBody VideoProgressDTO dto) {
        // dto: { userId, videoId, percent }
        videoProgressService.saveOrUpdate(dto);
        return ResponseEntity.ok().build();
    }
}
