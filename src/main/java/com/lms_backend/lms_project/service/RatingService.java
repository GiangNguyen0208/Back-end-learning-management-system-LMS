package com.lms_backend.lms_project.service;

import com.lms_backend.lms_project.dto.request.RatingRequest;
import com.lms_backend.lms_project.dto.response.RatingListResponse;
import com.lms_backend.lms_project.dto.response.RatingResponse;

public interface RatingService {
    RatingListResponse fetchRatingsByCourse(int courseId);
    RatingResponse addRating(RatingRequest request);
}
