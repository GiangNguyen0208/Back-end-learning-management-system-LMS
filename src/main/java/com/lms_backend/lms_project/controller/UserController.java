package com.lms_backend.lms_project.controller;

import com.lms_backend.lms_project.dto.request.AddMentorDetailRequestDto;
import com.lms_backend.lms_project.dto.request.UserLoginRequest;
import com.lms_backend.lms_project.dto.response.CommonApiResponse;
import com.lms_backend.lms_project.dto.response.RegisterUserRequestDTO;
import com.lms_backend.lms_project.dto.response.UserLoginResponse;
import com.lms_backend.lms_project.resource.UserResource;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/user")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {
    @Autowired
    UserResource userResource;

    @PostMapping("/login")
    @Operation(summary = "Api to login any User")
    public ResponseEntity<UserLoginResponse> login(@RequestBody UserLoginRequest userLoginRequest) {
        return userResource.login(userLoginRequest);
    }

    @PostMapping("/register")
    public ResponseEntity<CommonApiResponse> register(@RequestBody RegisterUserRequestDTO request){
        return userResource.registerUser(request);
    }

    @PutMapping("/mentor/detail/update")
    @Operation(summary = "Api to update the mentor detail")
    public ResponseEntity<CommonApiResponse> addMentorDetail(AddMentorDetailRequestDto addMentorDetailRequestDto) {
        return this.userResource.addMentorDetail(addMentorDetailRequestDto);
    }


    @GetMapping(path = "/confirm")
    public ResponseEntity<CommonApiResponse> confirm(@RequestParam("token") String token) {
        return userResource.confirmToken(token);
    }

    @GetMapping(path = "/resend-confirmation")
    public ResponseEntity<CommonApiResponse> resendConfirm(@RequestParam("token") String email) {
        return userResource.resendConfirmToken(email);
    }
}
