package com.lms_backend.lms_project.controller;

import com.lms_backend.lms_project.Utility.Constant;
import com.lms_backend.lms_project.Utility.Helper;
import com.lms_backend.lms_project.Utility.JwtUtils;
import com.lms_backend.lms_project.Utility.OtpStore;
import com.lms_backend.lms_project.config.VNPayConfig;
import com.lms_backend.lms_project.dto.UserDTO;
import com.lms_backend.lms_project.dto.request.BookingFreeRequestDTO;
import com.lms_backend.lms_project.dto.request.BookingRequestDTO;
import com.lms_backend.lms_project.dto.response.BookingResponseDTO;
import com.lms_backend.lms_project.dto.response.CommonApiResponse;
import com.lms_backend.lms_project.dto.response.CourseResponseDto;
import com.lms_backend.lms_project.entity.Booking;
import com.lms_backend.lms_project.entity.Course;
import com.lms_backend.lms_project.entity.User;
import com.lms_backend.lms_project.resource.BookingResource;
import com.lms_backend.lms_project.resource.CourseResource;
import com.lms_backend.lms_project.service.BookingService;
import com.lms_backend.lms_project.service.CourseService;
import com.lms_backend.lms_project.service.EmailService;
import com.lms_backend.lms_project.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequestMapping("api/booking")
@CrossOrigin(origins = "http://localhost:5173")
public class BookingController {

    private final Logger LOG = LoggerFactory.getLogger(BookingResource.class);

    @Autowired
    private BookingResource bookingResource;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private CourseResource courseResource;

    @Autowired
    private CourseService courseService;

    @Autowired
    private BookingService bookingService;

    @PostMapping("add")
    public ResponseEntity<CommonApiResponse> addEvent(@RequestBody BookingRequestDTO request) {
        System.out.println("Received booking request: " + request);
        return this.bookingResource.addBooking(request);
    }
    @PostMapping("add-free")
    public ResponseEntity<CommonApiResponse> bookFreeCourse(@RequestBody BookingFreeRequestDTO request) {
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

    @GetMapping("/send-otp")
    public ResponseEntity<CommonApiResponse> sendOtpToEmail(@RequestParam("email") String email) {
        CommonApiResponse response = new CommonApiResponse();
        try {
            String otp = Helper.generateOtp();
            emailService.sendOtpEmail(email, otp);

            // Lưu vào OTP store
            OtpStore.saveOtp(email, otp);

            response.setResponseMessage("OTP đã được gửi tới email.");
            response.setSuccess(true);
        } catch (Exception e) {
            response.setResponseMessage("Error while sending OTP: " + e.getMessage());
            response.setSuccess(false);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/fetch/course-user-id")
    @Operation(summary = "Api to fetch course by using course id and student id")
    public ResponseEntity<CourseResponseDto> fetchCourseById(@RequestParam("courseId") Integer courseId,
                                                             @RequestParam("userId") Integer userId) {
        return courseResource.fetchCourseByIdAndUserId(courseId, userId);
    }

    @GetMapping("/students/{mentorId}/{courseId}")
    public ResponseEntity<List<UserDTO>> getStudentsByCourseAndMentor(
            @PathVariable int mentorId,
            @PathVariable int courseId) {

        List<UserDTO> students = bookingResource.getStudentsByCourseAndMentor(mentorId, courseId);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/fetch/student-by-course/{courseId}")
    @Operation(summary = "Api to fetch courses by using name")
    public List<User> fetchStudentByCourse(@PathVariable("courseId") int courseId) {
        return bookingResource.fetchStudentByCourse(courseId);
    }

    @GetMapping("/VNpay")
    public ResponseEntity<CommonApiResponse> getPay(
            @RequestParam(value = "amount", required = true) long amount,
            @RequestParam(value = "courseIds", required = true) String courseIds,
            @RequestParam(value = "customerId", required = true) Integer customerId
    ) throws UnsupportedEncodingException {
        CommonApiResponse response = new CommonApiResponse();

        // Validate input
        if (courseIds == null || courseIds.isEmpty() || customerId == null || amount <= 0) {
            response.setResponseMessage("Thiếu thông tin: courseIds, customerId hoặc amount không hợp lệ");
            response.setSuccess(false);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // Parse courseIds
        List<Integer> courseIdList = Arrays.stream(courseIds.split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        // Validate customer
        User customer = userService.getUserById(customerId);
        if (customer == null) {
            response.setResponseMessage("Không tìm thấy người dùng");
            response.setSuccess(false);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // Validate courses and calculate total amount
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Integer courseId : courseIdList) {
            Course course = courseService.getById(courseId);
            if (course == null) {
                response.setResponseMessage("Không tìm thấy khóa học với ID: " + courseId);
                response.setSuccess(false);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            BigDecimal discountPercent = new BigDecimal(course.getDiscountInPercent()).divide(new BigDecimal(100), 4, RoundingMode.HALF_UP);
            BigDecimal feeWithDiscount = course.getFee().multiply(BigDecimal.ONE.subtract(discountPercent));
            totalAmount = totalAmount.add(feeWithDiscount);

            // Check for duplicate booking
            List<Booking> existingBookings = bookingService.getBookingsByCourseAndCustomer(course, customer);
            if (!existingBookings.isEmpty()) {
                response.setResponseMessage("Khóa học với ID " + courseId + " đã được mua!");
                response.setSuccess(false);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        }

        // Validate amount
        BigDecimal requestedAmount = new BigDecimal(amount).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
        if (totalAmount.compareTo(requestedAmount) != 0) {
            response.setResponseMessage("Số tiền không khớp. Dự kiến: " + totalAmount);
            response.setSuccess(false);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // Create pending bookings
        String bookingId = Helper.generateTourBookingId();
        List<Booking> pendingBookings = new ArrayList<>();
        for (Integer courseId : courseIdList) {
            Course course = courseService.getById(courseId);
            Booking booking = new Booking();
            booking.setBookingId(bookingId);
            booking.setCourse(course);
            booking.setCustomer(customer);
            booking.setStatus(Constant.BookingStatus.CANCELLED.value());
            booking.setBookingTime(String.valueOf(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
            booking.setAmount(course.getFee());
            booking.setDiscountInPercent(course.getDiscountInPercent());
            bookingService.addBooking(booking); // Save pending booking
            pendingBookings.add(booking);
        }

        // Generate VNPay payment URL
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String orderType = "other";
        String bankCode = "NCB";
        String vnp_TxnRef = VNPayConfig.getRandomNumber(8);
        String vnp_IpAddr = "127.0.0.1";
        String vnp_TmnCode = VNPayConfig.vnp_TmnCode;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_BankCode", bankCode);
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef + ", bookingId: " + bookingId);
        vnp_Params.put("vnp_OrderType", orderType);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", VNPayConfig.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = VNPayConfig.vnp_PayUrl + "?" + queryUrl;

        response.setResponseMessage(paymentUrl);
        response.setSuccess(true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/vnpay-callback")
    public ResponseEntity<CommonApiResponse> handleVNPayCallback(
            @RequestParam Map<String, String> vnpParams) {
        CommonApiResponse response = new CommonApiResponse();

        // Verify secure hash
        String vnp_SecureHash = vnpParams.get("vnp_SecureHash");
        vnpParams.remove("vnp_SecureHash");
        List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnpParams.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (itr.hasNext()) {
                    hashData.append('&');
                }
            }
        }
        String calculatedHash = VNPayConfig.hmacSHA512(VNPayConfig.secretKey, hashData.toString());
        if (!calculatedHash.equals(vnp_SecureHash)) {
            response.setResponseMessage("Chữ ký không hợp lệ");
            response.setSuccess(false);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // Check transaction status
        String vnp_ResponseCode = vnpParams.get("vnp_ResponseCode");
        String vnp_TxnRef = vnpParams.get("vnp_TxnRef");
        String vnp_OrderInfo = vnpParams.get("vnp_OrderInfo");
        String bookingId = vnp_OrderInfo.split("bookingId: ")[1];

        if (!"00".equals(vnp_ResponseCode)) {
            // Transaction failed, update bookings to CANCELLED
            List<Booking> bookings = bookingService.getBookingsByBookingId(bookingId);
            for (Booking booking : bookings) {
                booking.setStatus(Constant.BookingStatus.CANCELLED.value());
                bookingService.updateBooking(booking);
            }
            response.setResponseMessage("Thanh toán thất bại: " + vnp_ResponseCode);
            response.setSuccess(false);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // Transaction successful, update bookings
        List<Booking> bookings = bookingService.getBookingsByBookingId(bookingId);
        for (Booking booking : bookings) {
            Course course = booking.getCourse();
            User customer = booking.getCustomer();
            User mentor = course.getMentor();

            // Update booking status
            booking.setStatus(Constant.BookingStatus.CONFIRMED.value());
            bookingService.updateBooking(booking);

            // Update course student count
            course.setQuantityStudent(course.getQuantityStudent() + 1);
            courseService.update(course);

            // Update mentor's amount
            mentor.setAmount(mentor.getAmount().add(course.getFee()));
            userService.updateUser(mentor);

            // Update mentor's total student count
            List<Course> courseRelative = courseService.getByMentorAndStatus(mentor, Constant.ActiveStatus.ACTIVE.value());
            int totalStudent = courseRelative.stream().mapToInt(Course::getQuantityStudent).sum();
            mentor.getMentorDetail().setQuantityStudent(totalStudent);
            userService.updateUser(mentor);
        }

        response.setResponseMessage("Thanh toán thành công!");
        response.setSuccess(true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

