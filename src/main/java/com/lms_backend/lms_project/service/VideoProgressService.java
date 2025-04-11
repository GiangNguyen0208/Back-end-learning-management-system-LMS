package com.lms_backend.lms_project.service;

import com.lms_backend.lms_project.dto.VideoProgressDTO;

public interface VideoProgressService {
    void saveOrUpdate(VideoProgressDTO dto);
}
