package com.shopee.shopee_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateFranchiseRequestDto {

    private String outletName;
    private String ownerName;
    private String email;
    private String mobile;
    private String address;
    private String city;
    private String state;
    private LocalDate validFrom;
    private LocalDate validTo;
}
