package com.lms_backend.lms_project.controller;

import com.lms_backend.lms_project.dto.request.BookingRequestDTO;
import com.lms_backend.lms_project.dto.response.BookingResponseDTO;
import com.lms_backend.lms_project.dto.response.CommonApiResponse;
import com.lms_backend.lms_project.resource.BookingResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/booking")
@CrossOrigin(origins = "http://localhost:5173")
public class BookingController {

    @Autowired
    private BookingResource bookingResource;

    @PostMapping("add")
    public ResponseEntity<CommonApiResponse> addEvent(@RequestBody BookingRequestDTO request) {
        return this.bookingResource.addBooking(request);
    }
    @PostMapping("add-free")
    public ResponseEntity<CommonApiResponse> bookFreeCourse(@RequestBody BookingRequestDTO request) {
        return this.bookingResource.bookFreeCourse(request);
    }

    @GetMapping("fetch/all")
    public ResponseEntity<BookingResponseDTO> fetchAllBookings() {
        return this.bookingResource.fetchAllBookings();
    }

    @GetMapping("fetch/course-wise")
    public ResponseEntity<BookingResponseDTO> fetchAllBookingsByCourse(@RequestParam("courseId") Integer courseId) {
        return this.bookingResource.fetchAllBookingsByCourse(courseId);
    }

    @GetMapping("fetch/customer-wise")
    public ResponseEntity<BookingResponseDTO> fetchAllBookingsByCustomer(
            @RequestParam("customerId") Integer customerId) {
        return this.bookingResource.fetchAllBookingsByCustomer(customerId);
    }

    @GetMapping("fetch/mentor-wise")
    public ResponseEntity<BookingResponseDTO> fetchAllBookingsByMentorId(
            @RequestParam("mentorId") Integer mentorId) {
        return this.bookingResource.fetchAllBookingsByMentorId(mentorId);
    }

}

