package com.lms_backend.lms_project.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingRequestDTO {
    private int courseId;

    private int customerId;

    private String cardNo;

    private String nameOnCard;

    private String cvv;

    private String expiryDate;

    private BigDecimal amount;
}
