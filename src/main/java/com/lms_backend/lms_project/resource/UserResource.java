package com.lms_backend.lms_project.resource;

import com.lms_backend.lms_project.Utility.Constant;
import com.lms_backend.lms_project.dto.CommonApiResponse;
import com.lms_backend.lms_project.dto.RegisterUserRequestDTO;
import com.lms_backend.lms_project.entity.ConfirmationToken;
import com.lms_backend.lms_project.entity.User;
import com.lms_backend.lms_project.exception.UserSaveFailedException;
import com.lms_backend.lms_project.service.EmailService;
import com.lms_backend.lms_project.service.UserService;
import com.lms_backend.lms_project.serviceimpl.ConfirmationTokenService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@Transactional
public class UserResource {
    private final Logger LOG = LoggerFactory.getLogger(UserResource.class);

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ConfirmationTokenService confirmationTokenService;

    @Autowired
    private EmailService emailService;

    public ResponseEntity<CommonApiResponse> registerUser(RegisterUserRequestDTO request) {

        LOG.info("Request received for Register User");

        CommonApiResponse response = new CommonApiResponse();

        if (request == null) {
            response.setResponseMessage("user is null");
            response.setSuccess(false);

            return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
        }

        if (request.getEmailId() == null || request.getPassword() == null) {
            response.setResponseMessage("missing input");
            response.setSuccess(false);

            return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
        }

        // User existed and actived.
        User existingUser = this.userService.getUserByEmailAndStatus(request.getEmailId(), Constant.ActiveStatus.ACTIVE.value());

        if (existingUser != null) {
            response.setResponseMessage("User adready register with this Email");
            response.setSuccess(false);

            return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
        }

        if (request.getRole() == null) {
            response.setResponseMessage("bad request ,Role is missing");
            response.setSuccess(false);

            return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
        }

        // User existed but deactived.
        User userDeactive = this.userService.getUserByEmailAndStatus(request.getEmailId(), Constant.ActiveStatus.DEACTIVATED.value());
        if (userDeactive != null) {
            response.setResponseMessage("Please Confirm Email to active account to login");
            response.setSuccess(false);
        }


        // Register User Input Form
        User user = RegisterUserRequestDTO.toUserEntity(request);
        LocalDateTime now = LocalDateTime.now();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUsername(request.getUsername());
        user.setAmount(BigDecimal.ZERO);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus(Constant.ActiveStatus.DEACTIVATED.value());
        user.setCreatedAt(now);
        user.setRole(Constant.UserRole.ROLE_STUDENT.value());
        User userActive = userService.addUser(user);

        String token = userService.generateToken(user);
        String link = "http://localhost:8080/api/v1/registration/confirm?token=" + token;
        emailService.send(
                user.getEmailId(),
                buildEmail(user.getUsername(), link));

        if (userActive == null) {
            throw new UserSaveFailedException("Registration Failed because of Technical issue:(");
        }

        response.setResponseMessage("User registered Successfully");
        response.setSuccess(true);

        return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);
    }

    private String buildEmail(String username, String link) {
        return "<div style=\"font-family: Arial, sans-serif; font-size: 16px; color: #333; line-height: 1.6;\">"
                + "<h2 style=\"color: #1a73e8;\">Chào " + username + ",</h2>"
                + "<p>Cảm ơn bạn đã đăng ký tài khoản tại hệ thống của chúng tôi.</p>"
                + "<p>Vui lòng xác nhận email của bạn bằng cách nhấp vào nút bên dưới:</p>"
                + "<p style=\"text-align: center;\">"
                + "<a href=\"" + link + "\" style=\"display: inline-block; padding: 12px 24px; color: #fff; background-color: #1a73e8; text-decoration: none; border-radius: 5px; font-weight: bold;\">Xác nhận email</a>"
                + "</p>"
                + "<p>Nếu bạn không thực hiện đăng ký này, vui lòng bỏ qua email này.</p>"
                + "<p>Trân trọng,<br><strong>Đội ngũ hỗ trợ LMS</strong></p>"
                + "</div>";
    }

    public ResponseEntity<CommonApiResponse> confirmToken(String token) {
        LOG.info("Confirm mail");

        CommonApiResponse response = new CommonApiResponse();

        ConfirmationToken confirmationToken = confirmationTokenService
                .getToken(token)
                .orElseThrow(() -> new IllegalStateException("token not found"));

        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("email already confirmed");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("token expired");
        }

        confirmationTokenService.setConfirmedAt(token);
        userService.activeUser(
                confirmationToken.getUser().getEmailId());

        response.setResponseMessage("Confirm Email Successfully, token is: " + token);
        response.setSuccess(true);

        return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);
    }
}
