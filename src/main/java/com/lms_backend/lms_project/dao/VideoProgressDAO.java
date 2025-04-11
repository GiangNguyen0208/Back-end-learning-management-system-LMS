package com.lms_backend.lms_project.dao;

import com.lms_backend.lms_project.entity.VideoProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VideoProgressDAO extends JpaRepository<VideoProgress, Integer> {
    Optional<VideoProgress> findByUserIdAndVideoId(int userId, int videoId);
}