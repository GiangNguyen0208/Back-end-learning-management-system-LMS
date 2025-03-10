package com.lms_backend.lms_project.dao;

import com.lms_backend.lms_project.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressDAO extends JpaRepository<Address, Integer> {
}
