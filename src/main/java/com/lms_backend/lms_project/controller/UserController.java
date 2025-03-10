package com.lms_backend.lms_project.controller;

import com.lms_backend.lms_project.dto.CommonApiResponse;
import com.lms_backend.lms_project.dto.RegisterUserRequestDTO;
import com.lms_backend.lms_project.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/user")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {
    @Autowired
    UserResource userResource;

    @PostMapping("/register")
    public ResponseEntity<CommonApiResponse> register(@RequestBody RegisterUserRequestDTO request){
        return userResource.registerUser(request);
    }

    @GetMapping(path = "/confirm")
    public ResponseEntity<CommonApiResponse> confirm(@RequestParam("token") String token) {
        return userResource.confirmToken(token);
    }
}
