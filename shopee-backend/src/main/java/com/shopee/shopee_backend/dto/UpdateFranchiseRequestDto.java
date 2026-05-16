package com.shopee.shopee_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFranchiseRequestDto {

    private String outletName;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private String phone;
    private LocalDate validFrom;
    private LocalDate validTo;
    private String subscriptionPlan;
}
