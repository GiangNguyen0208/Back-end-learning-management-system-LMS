package com.lms_backend.lms_project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingListResponse {
    private List<RatingResponse> ratings;
    private Double averageRating;
    private Long totalRatings;
}
