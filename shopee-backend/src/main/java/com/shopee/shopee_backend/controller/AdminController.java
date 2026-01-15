package com.shopee.shopee_backend.controller;

import com.shopee.shopee_backend.dto.CreateFranchiseRequestDto;
import com.shopee.shopee_backend.dto.CreateFranchiseResponseDto;
import com.shopee.shopee_backend.dto.FranchiseDto;
import com.shopee.shopee_backend.dto.UserDto;
import com.shopee.shopee_backend.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/franchise")
    public ResponseEntity<List<FranchiseDto>> getFranchiseList(){
        return ResponseEntity.ok(adminService.getAllFranchise());
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers(){
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PostMapping("/franchise")
    public ResponseEntity<CreateFranchiseResponseDto> createFranchise(@RequestBody CreateFranchiseRequestDto createFranchiseRequestDto){
        return ResponseEntity.ok(adminService.createFranchise(createFranchiseRequestDto));
    }


}
