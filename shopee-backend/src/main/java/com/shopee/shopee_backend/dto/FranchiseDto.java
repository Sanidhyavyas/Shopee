package com.shopee.shopee_backend.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FranchiseDto {
    private Long id;
    private String outletName;
    private String city;
    private String state;
    private String email;
    private String mobile;
}




