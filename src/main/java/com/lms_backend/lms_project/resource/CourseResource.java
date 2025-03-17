package com.lms_backend.lms_project.resource;

import com.lms_backend.lms_project.Utility.Constant;
import com.lms_backend.lms_project.dto.request.AddCourseRequestDto;
import com.lms_backend.lms_project.dto.request.AddCourseSectionRequestDto;
import com.lms_backend.lms_project.dto.request.AddCourseSectionTopicRequest;
import com.lms_backend.lms_project.dto.response.CourseResponseDto;
import com.lms_backend.lms_project.entity.*;
import com.lms_backend.lms_project.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;


@Component
public class CourseResource {
    private final Logger LOG = LoggerFactory.getLogger(CourseResource.class);

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseSectionService courseSectionService;

    @Autowired
    private CourseSectionTopicService courseSectionTopicService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;

    @Autowired
    private StorageService storageService;

    public ResponseEntity<CourseResponseDto> addCourse(AddCourseRequestDto request) {

        LOG.info("received request for adding the mentor course");

        CourseResponseDto response = new CourseResponseDto();

        if (request == null) {
            response.setResponseMessage("missing request body");
            response.setSuccess(false);

            return new ResponseEntity<CourseResponseDto>(response, HttpStatus.BAD_REQUEST);
        }

        if (request.getCategoryId() == 0 || request.getDescription() == null || request.getMentorId() == 0
                || request.getName() == null || request.getNotesFileName() == null || request.getType() == null
                || request.getThumbnail() == null) {
            response.setResponseMessage("missing input");
            response.setSuccess(false);

            return new ResponseEntity<CourseResponseDto>(response, HttpStatus.BAD_REQUEST);
        }

        String addedDateTime = String
                .valueOf(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());

        Category category = this.categoryService.getCategoryById(request.getCategoryId());

        if (category == null) {
            response.setResponseMessage("category not found");
            response.setSuccess(false);

            return new ResponseEntity<CourseResponseDto>(response, HttpStatus.BAD_REQUEST);
        }

        User mentor = this.userService.getUserById(request.getMentorId());

        if (mentor == null) {
            response.setResponseMessage("mentor not found");
            response.setSuccess(false);

            return new ResponseEntity<CourseResponseDto>(response, HttpStatus.BAD_REQUEST);
        }

        Course course = AddCourseRequestDto.toEntity(request);

        String courseNote = this.storageService.storeCourseNote(request.getNotesFileName());
        String thumbnailFilename = this.storageService.storeCourseNote(request.getThumbnail());

        course.setThumbnail(thumbnailFilename);
        course.setAddedDateTime(addedDateTime);
        course.setNotesFileName(courseNote);
        course.setCategory(category);
        course.setMentor(mentor);
        course.setStatus(Constant.ActiveStatus.ACTIVE.value());

        Course savedCourse = this.courseService.add(course);

        if (savedCourse == null) {
            response.setResponseMessage("Failed to add the course");
            response.setSuccess(false);

            return new ResponseEntity<CourseResponseDto>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            response.setCourse(savedCourse);
            response.setResponseMessage("Course Created Successful, Add Course Section now....");
            response.setSuccess(true);

            return new ResponseEntity<CourseResponseDto>(response, HttpStatus.OK);
        }
    }

    public ResponseEntity<CourseResponseDto> addCourseSection(AddCourseSectionRequestDto request) {

        LOG.info("received request for adding the course section");

        CourseResponseDto response = new CourseResponseDto();

        if (request == null) {
            response.setResponseMessage("missing request body");
            response.setSuccess(false);

            return new ResponseEntity<CourseResponseDto>(response, HttpStatus.BAD_REQUEST);
        }

        if (request.getCourseId() == 0 || request.getName() == null || request.getDescription() == null
                || request.getSrNo() == null) {
            response.setResponseMessage("missing input");
            response.setSuccess(false);

            return new ResponseEntity<CourseResponseDto>(response, HttpStatus.BAD_REQUEST);
        }

        Course course = this.courseService.getById(request.getCourseId());

        if (course == null) {
            response.setResponseMessage("course not found!!!");
            response.setSuccess(false);

            return new ResponseEntity<CourseResponseDto>(response, HttpStatus.BAD_REQUEST);
        }

        CourseSection section = new CourseSection();

        section.setCourse(course);
        section.setDescription(request.getDescription());
        section.setName(request.getName());
        section.setSrNo(request.getSrNo());

        CourseSection savedSection = this.courseSectionService.add(section);

        if (savedSection == null) {
            response.setCourse(course);
            response.setResponseMessage("Failed to add the course section");
            response.setSuccess(false);

            return new ResponseEntity<CourseResponseDto>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } else {

            Course updatedCourse = this.courseService.getById(request.getCourseId());

            response.setCourse(updatedCourse);
            response.setResponseMessage("Course Section Added successful!!!");
            response.setSuccess(true);

            return new ResponseEntity<CourseResponseDto>(response, HttpStatus.OK);
        }

    }

    public ResponseEntity<CourseResponseDto> addCourseSectionTopic(AddCourseSectionTopicRequest request) {

        LOG.info("received request for adding the course section");

        CourseResponseDto response = new CourseResponseDto();

        if (request == null) {
            response.setResponseMessage("missing request body");
            response.setSuccess(false);

            return new ResponseEntity<CourseResponseDto>(response, HttpStatus.BAD_REQUEST);
        }

        if (request.getSectionId() == 0 || request.getName() == null || request.getDescription() == null
                || request.getSrNo() == null || request.getVideo() == null) {
            response.setResponseMessage("missing input");
            response.setSuccess(false);

            return new ResponseEntity<CourseResponseDto>(response, HttpStatus.BAD_REQUEST);
        }

        CourseSection section = this.courseSectionService.getById(request.getSectionId());

        if (section == null) {
            response.setResponseMessage("Course Section not found!!!");
            response.setSuccess(false);

            return new ResponseEntity<CourseResponseDto>(response, HttpStatus.BAD_REQUEST);
        }

        CourseSectionTopic topic = new CourseSectionTopic();
        topic.setName(request.getName());
        topic.setSrNo(request.getSrNo());
        topic.setDescription(request.getDescription());
        topic.setCourseSection(section);

        String topicVideoFileName = this.storageService.storeCourseVideo(request.getVideo());

        topic.setVideoFileName(topicVideoFileName);

        CourseSectionTopic savedTopic = this.courseSectionTopicService.add(topic);

        Course updatedCourse = this.courseService.getById(section.getCourse().getId());

        if (savedTopic == null) {
            response.setCourse(updatedCourse);
            response.setResponseMessage("Failed to add the course section topic");
            response.setSuccess(false);

            return new ResponseEntity<CourseResponseDto>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            response.setCourse(updatedCourse);
            response.setResponseMessage("Course Section Topic Added successful!!!");
            response.setSuccess(true);

            return new ResponseEntity<CourseResponseDto>(response, HttpStatus.OK);
        }

    }

}
