package com.shopee.shopee_backend.service;

import com.shopee.shopee_backend.dto.UserDto;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface AdminService {
     public List<UserDto>  getAllFranchise();
     public List<UserDto> getAllUsers();
}
