package com.shopee.shopee_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateFranchiseResponseDto {

    private Long franchiseId;
    private String email;
    private String temporaryPassword;
    private String message;
}
