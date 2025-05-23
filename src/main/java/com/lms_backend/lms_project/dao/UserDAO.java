package com.lms_backend.lms_project.dao;

import com.lms_backend.lms_project.Utility.Constant;
import com.lms_backend.lms_project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserDAO extends JpaRepository<User, Integer> {
    User findByEmailId(String email);

    User findByEmailIdAndStatus(String email, String status);

    User findByRoleAndStatusIn(String role, List<String> status);

    List<User> findByRole(String role);

    User findByEmailIdAndRoleAndStatus(String emailId, String role, String status);

    List<User> findByRoleAndStatus(String role, String status);

    @Query("SELECT u FROM User u WHERE u.status = :status AND u.username = :username")
    User findByUsernameAndStatus(@Param("username") String username, @Param("status") String status);

    @Transactional
    @Modifying
    @Query("UPDATE User a SET a.status = 'Active' WHERE a.emailId = ?1")
    int activeUser(String email);

    @Query("SELECT u FROM User u WHERE u.mentorDetail IS NOT NULL AND u.role = 'Mentor'")
    List<User> findAllMentors();

    @Query("SELECT u FROM User u WHERE u.oauth2_id = :oauth2Id")
    User findByOauth2Id(@Param("oauth2Id") String oauth2Id);

    @Query("SELECT u FROM User u WHERE u.role = 'Mentor' AND u.id = :mentorID AND u.status = 'Active'")
    User getMentorByID(@Param("mentorID") int mentorID);
}
