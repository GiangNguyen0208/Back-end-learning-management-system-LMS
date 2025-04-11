package com.lms_backend.lms_project.serviceimpl;

import com.lms_backend.lms_project.dao.VideoProgressDAO;
import com.lms_backend.lms_project.dto.VideoProgressDTO;
import com.lms_backend.lms_project.entity.VideoProgress;
import com.lms_backend.lms_project.service.VideoProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VideoProgressServiceImpl implements VideoProgressService {
    @Autowired
    private VideoProgressDAO videoProgressDAO;

    @Override
    public void saveOrUpdate(VideoProgressDTO dto) {
        VideoProgress progress = videoProgressDAO.findByUserIdAndVideoId(dto.getUserId(), dto.getVideoId())
                .map(existing -> {
                    existing.setPercentWatched(Math.max(existing.getPercentWatched(), dto.getPercentWatched()));
                    return existing;
                })
                .orElse(VideoProgress.builder()
                        .userId(dto.getUserId())
                        .videoId(dto.getVideoId())
                        .percentWatched(dto.getPercentWatched())
                        .build());

        videoProgressDAO.save(progress);
    }
}
