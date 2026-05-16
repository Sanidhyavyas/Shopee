package com.shopee.shopee_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StaffDto {

    private Long userId;
    private String name;
    private String email;
    private String mobile;
    private boolean active;
    private Long assignedFranchiseId;
    private String assignedFranchiseName;
}
