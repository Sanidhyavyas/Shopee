package com.shopee.shopee_backend.service;

import com.shopee.shopee_backend.dto.CreateStaffRequestDto;
import com.shopee.shopee_backend.dto.StaffDto;

import java.util.List;

public interface StaffService {

    List<StaffDto> getStaffByFranchise(Long franchiseId);

    StaffDto createStaff(Long franchiseId, CreateStaffRequestDto request);

    void removeStaff(Long franchiseId, Long staffId);
}
