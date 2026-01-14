package com.shopee.shopee_backend.service.impl;

import com.shopee.shopee_backend.dto.UserDto;
import com.shopee.shopee_backend.repository.UserRepository;
import com.shopee.shopee_backend.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final UserDto userDto;
    private final ModelMapper modelMapper;

    @Override
    public List<UserDto> getAllFranchise(){

    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(user -> modelMapper.map(user, UserDto.class)).toList();
    }

}
