package com.lms_backend.lms_project.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingRequest {
    @NotNull(message = "User ID không được để trống")
    private Long userId;

    @NotNull(message = "Course ID không được để trống")
    private Long courseId;

    @NotNull(message = "Rating không được để trống")
    @Min(1) @Max(5)
    private Integer rating;

    @NotBlank(message = "Comment không được để trống")
    @Size(max = 1000, message = "Comment không được quá 1000 ký tự")
    private String comment;
}