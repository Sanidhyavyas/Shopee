package com.shopee.shopee_backend.controller;

import com.shopee.shopee_backend.dto.UserDto;
import com.shopee.shopee_backend.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/view-franchise")
    public ResponseEntity<List<UserDto>> getFranchiseList(){
        return ResponseEntity.ok(adminService.getAllFranchise());
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers(){
        return ResponseEntity.ok(adminService.getAllUsers());
    }
}
