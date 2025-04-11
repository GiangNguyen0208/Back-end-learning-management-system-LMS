package com.lms_backend.lms_project.dto;

import lombok.Data;

@Data
public class VideoProgressDTO {
    private int userId;
    private int videoId;
    private int percentWatched;
}
