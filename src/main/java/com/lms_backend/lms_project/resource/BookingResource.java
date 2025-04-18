package com.lms_backend.lms_project.resource;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import com.lms_backend.lms_project.Utility.Constant;
import com.lms_backend.lms_project.Utility.Helper;
import com.lms_backend.lms_project.dto.request.BookingRequestDTO;
import com.lms_backend.lms_project.dto.response.BookingResponseDTO;
import com.lms_backend.lms_project.dto.response.CommonApiResponse;
import com.lms_backend.lms_project.entity.Booking;
import com.lms_backend.lms_project.entity.Course;
import com.lms_backend.lms_project.entity.Payment;
import com.lms_backend.lms_project.entity.User;
import com.lms_backend.lms_project.service.BookingService;
import com.lms_backend.lms_project.service.CourseService;
import com.lms_backend.lms_project.service.PaymentService;
import com.lms_backend.lms_project.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class BookingResource {

    private final Logger LOG = LoggerFactory.getLogger(BookingResource.class);

    @Autowired
    private BookingService bookingService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserService userService;

    @Autowired
    private PaymentService paymentService;

    public ResponseEntity<CommonApiResponse> addBooking(BookingRequestDTO request) {

        LOG.info("request received for adding customer booking course");

        CommonApiResponse response = new CommonApiResponse();

        String bookingTime = String
                .valueOf(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());

        if (request == null) {
            response.setResponseMessage("missing input");
            response.setSuccess(false);

            return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
        }

        if (request.getCourseId() == 0 || request.getCustomerId() == 0 || request.getCvv() == null
                || request.getExpiryDate() == null || request.getNameOnCard() == null || request.getCardNo() == null
                || request.getAmount() == null) {

            response.setResponseMessage("missing input or invalid details");
            response.setSuccess(false);

            return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);

        }

        User customer = this.userService.getUserById(request.getCustomerId());

        if (customer == null) {
            response.setResponseMessage("customer not found!!!");
            response.setSuccess(false);

            return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
        }

        Course course = this.courseService.getById(request.getCourseId());

        if (course == null) {
            response.setResponseMessage("course not found!!!");
            response.setSuccess(false);

            return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
        }

        List<Booking> bookings = this.bookingService.getByCourseAndCustomer(course, customer);

        if (!CollectionUtils.isEmpty(bookings)) {
            response.setResponseMessage("Course Already Purchased!!!");
            response.setSuccess(false);

            return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
        }

        String bookingId = Helper.generateTourBookingId();
        String paymentBookingId = Helper.generateBookingPaymentId();

        Payment payment = new Payment();
        payment.setCardNo(request.getCardNo());
        payment.setBookingId(bookingId);
        payment.setAmount(request.getAmount());
        payment.setCustomer(customer);
        payment.setCvv(request.getCvv());
        payment.setExpiryDate(request.getExpiryDate());
        payment.setNameOnCard(request.getNameOnCard());
        payment.setPaymentId(paymentBookingId);

        Payment savedPayment = this.paymentService.addPayment(payment);

        if (savedPayment == null) {
            response.setResponseMessage("Failed to Purchase the Course, Payment Failure!!!");
            response.setSuccess(false);

            return new ResponseEntity<CommonApiResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        User mentor = course.getMentor();
        mentor.setAmount(mentor.getAmount().add(request.getAmount()));

        this.userService.updateUser(mentor);

        Booking booking = new Booking();
        booking.setBookingId(bookingId);
        booking.setPayment(savedPayment);
        booking.setAmount(request.getAmount());
        booking.setBookingTime(bookingTime);
        booking.setCustomer(customer);
        booking.setCourse(course);
        booking.setStatus(Constant.BookingStatus.CONFIRMED.value());

        Booking savedBooking = this.bookingService.addBooking(booking);

        if (savedBooking == null) {
            response.setResponseMessage("Failed to Book Event, Internal Error");
            response.setSuccess(false);

            return new ResponseEntity<CommonApiResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.setResponseMessage("Congralutions, Course Purchased!!!");
        response.setSuccess(true);

        return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);

    }

    public ResponseEntity<BookingResponseDTO> fetchAllBookings() {

        BookingResponseDTO response = new BookingResponseDTO();

        List<Booking> bookings = this.bookingService.getAllBookings();

        if (CollectionUtils.isEmpty(bookings)) {
            response.setResponseMessage("No Course Purchases found!!");
            response.setSuccess(false);

            return new ResponseEntity<BookingResponseDTO>(response, HttpStatus.BAD_REQUEST);
        }

        response.setBookings(bookings);
        response.setResponseMessage("Fetched all Course Purchases!!!");
        response.setSuccess(true);

        return new ResponseEntity<BookingResponseDTO>(response, HttpStatus.OK);
    }

    public ResponseEntity<BookingResponseDTO> fetchAllBookingsByCourse(Integer courseId) {

        BookingResponseDTO response = new BookingResponseDTO();

        if (courseId == null) {
            response.setResponseMessage("missing input");
            response.setSuccess(false);

            return new ResponseEntity<BookingResponseDTO>(response, HttpStatus.BAD_REQUEST);

        }

        Course course = this.courseService.getById(courseId);

        if (course == null) {
            response.setResponseMessage("course not found!!!");
            response.setSuccess(false);

            return new ResponseEntity<BookingResponseDTO>(response, HttpStatus.BAD_REQUEST);
        }

        List<Booking> bookings = this.bookingService.getByCourse(course);

        if (CollectionUtils.isEmpty(bookings)) {
            response.setResponseMessage("No Course Purchases found!!");
            response.setSuccess(false);

            return new ResponseEntity<BookingResponseDTO>(response, HttpStatus.OK);
        }

        response.setBookings(bookings);
        response.setResponseMessage("Fetched all Course Purchases!!!");
        response.setSuccess(true);

        return new ResponseEntity<BookingResponseDTO>(response, HttpStatus.OK);
    }

    public ResponseEntity<BookingResponseDTO> fetchAllBookingsByCustomer(Integer customerId) {

        BookingResponseDTO response = new BookingResponseDTO();

        if (customerId == null) {
            response.setResponseMessage("missing input");
            response.setSuccess(false);

            return new ResponseEntity<BookingResponseDTO>(response, HttpStatus.BAD_REQUEST);

        }

        User customer = this.userService.getUserById(customerId);

        if (customer == null) {
            response.setResponseMessage("customer not found!!!");
            response.setSuccess(false);

            return new ResponseEntity<BookingResponseDTO>(response, HttpStatus.BAD_REQUEST);
        }

        List<Booking> bookings = this.bookingService.getBookingByCustomer(customer);

        if (CollectionUtils.isEmpty(bookings)) {
            response.setResponseMessage("No Course Purchases found");
            response.setSuccess(false);

            return new ResponseEntity<BookingResponseDTO>(response, HttpStatus.OK);
        }

        response.setBookings(bookings);
        response.setResponseMessage("Fetched Course Purchases!!");
        response.setSuccess(true);

        return new ResponseEntity<BookingResponseDTO>(response, HttpStatus.OK);
    }

    public ResponseEntity<BookingResponseDTO> fetchAllBookingsByMentorId(Integer mentorId) {

        BookingResponseDTO response = new BookingResponseDTO();

        if (mentorId == null) {
            response.setResponseMessage("missing input");
            response.setSuccess(false);

            return new ResponseEntity<BookingResponseDTO>(response, HttpStatus.BAD_REQUEST);

        }

        User mentor = this.userService.getUserById(mentorId);

        if (mentor == null) {
            response.setResponseMessage("mentor not found!!!");
            response.setSuccess(false);

            return new ResponseEntity<BookingResponseDTO>(response, HttpStatus.BAD_REQUEST);
        }

        List<Booking> bookings = this.bookingService.getByMentor(mentor);

        if (CollectionUtils.isEmpty(bookings)) {
            response.setResponseMessage("No Course Purchases found");
            response.setSuccess(false);

            return new ResponseEntity<BookingResponseDTO>(response, HttpStatus.OK);
        }

        response.setBookings(bookings);
        response.setResponseMessage("Fetched all Course Purchases!!");
        response.setSuccess(true);

        return new ResponseEntity<BookingResponseDTO>(response, HttpStatus.OK);
    }

    public ResponseEntity<CommonApiResponse> bookFreeCourse(BookingRequestDTO request) {
        LOG.info("Request received to book FREE course");

        CommonApiResponse response = new CommonApiResponse();

        if (request == null || request.getCourseId() == 0 || request.getCustomerId() == 0) {
            response.setResponseMessage("Missing courseId or customerId");
            response.setSuccess(false);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        Course course = this.courseService.getById(request.getCourseId());
        if (course == null) {
            response.setResponseMessage("Course not found");
            response.setSuccess(false);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        if (course.getFee().compareTo(BigDecimal.ZERO) > 0) {
            response.setResponseMessage("This course is not free");
            response.setSuccess(false);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        User customer = this.userService.getUserById(request.getCustomerId());
        if (customer == null) {
            response.setResponseMessage("Customer not found");
            response.setSuccess(false);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        List<Booking> existingBookings = this.bookingService.getByCourseAndCustomer(course, customer);
        if (!CollectionUtils.isEmpty(existingBookings)) {
            response.setResponseMessage("You already enrolled in this course");
            response.setSuccess(false);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        Booking booking = new Booking();
        booking.setBookingId(Helper.generateTourBookingId());
        booking.setCourse(course);
        booking.setCustomer(customer);
        booking.setStatus(Constant.BookingStatus.CONFIRMED.value());
        booking.setBookingTime(String.valueOf(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
        booking.setAmount(BigDecimal.ZERO);  // Free

        Booking savedBooking = this.bookingService.addBooking(booking);
        if (savedBooking == null) {
            response.setResponseMessage("Failed to book free course");
            response.setSuccess(false);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.setResponseMessage("Successfully enrolled in the free course!");
        response.setSuccess(true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}

