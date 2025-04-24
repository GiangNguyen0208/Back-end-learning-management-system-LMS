package com.lms_backend.lms_project.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingRequestDTO {
    private List<Integer> courseIds;

    private int customerId;

    private String cardNo;

    private String nameOnCard;

    private String cvv;

    private String expiryDate;

    private BigDecimal amount;

    private String otpConfirm;
}
