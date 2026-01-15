package com.shopee.shopee_backend.service;

import com.shopee.shopee_backend.dto.CreateFranchiseRequestDto;
import com.shopee.shopee_backend.dto.CreateFranchiseResponseDto;
import com.shopee.shopee_backend.dto.FranchiseDto;
import com.shopee.shopee_backend.dto.UserDto;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface AdminService {
     public List<FranchiseDto>  getAllFranchise();
     public List<UserDto> getAllUsers();
     public CreateFranchiseResponseDto createFranchise(CreateFranchiseRequestDto request);
}
