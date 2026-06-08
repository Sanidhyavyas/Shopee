package com.shopee.shopee_backend.service;

import com.shopee.shopee_backend.dto.CreateStaffRequestDto;
import com.shopee.shopee_backend.dto.StaffDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StaffService {

    Page<StaffDto> getStaffByFranchise(Long franchiseId, Pageable pageable);

    StaffDto createStaff(Long franchiseId, CreateStaffRequestDto request);

    void removeStaff(Long franchiseId, Long staffId);
}
