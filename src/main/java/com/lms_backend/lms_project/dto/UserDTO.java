package com.lms_backend.lms_project.dto;

import com.lms_backend.lms_project.entity.Address;
import com.lms_backend.lms_project.entity.MentorDetail;
import com.lms_backend.lms_project.entity.User;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class UserDTO {
    private int id;

    private String firstName;

    private String lastName;
    private String emailId;

    private String phoneNo;

    private String role;

    private Address address;

    private MentorDetail mentorDetail;

    private String status;

    public static UserDTO toUserDtoEntity(User user) {
        UserDTO userDto = new UserDTO();
        BeanUtils.copyProperties(user, userDto);
        return userDto;
    }
}
