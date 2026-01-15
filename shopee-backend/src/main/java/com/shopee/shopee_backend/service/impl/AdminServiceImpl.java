package com.shopee.shopee_backend.service.impl;

import com.shopee.shopee_backend.dto.CreateFranchiseRequestDto;
import com.shopee.shopee_backend.dto.CreateFranchiseResponseDto;
import com.shopee.shopee_backend.dto.FranchiseDto;
import com.shopee.shopee_backend.dto.UserDto;
import com.shopee.shopee_backend.entity.Franchise;
import com.shopee.shopee_backend.entity.User;
import com.shopee.shopee_backend.repository.FranchiseRepository;
import com.shopee.shopee_backend.repository.UserRepository;
import com.shopee.shopee_backend.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final FranchiseRepository franchiseRepository;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder passwordEncoder;         //SecurityConfig bean injected

    @Override
    public List<FranchiseDto> getAllFranchise(){
        return franchiseRepository.findAll().stream().map(franchise -> modelMapper.map(franchise, FranchiseDto.class)).toList();
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(user -> modelMapper.map(user, UserDto.class)).toList();
    }

    @Override
    public CreateFranchiseResponseDto createFranchise(CreateFranchiseRequestDto request) {

        // Check if an owner already exists
        User owner = userRepository.findByEmail(request.getEmail()).orElse(null);

        String rawPassword = null;

        //If the owner does not exist, create one
        if(owner == null){
            owner = new User();
            owner.setEmail(request.getEmail());
            owner.setRole("FRANCHISE ADMIN");
            owner.setActive(true);

            rawPassword = generatePassword();
            owner.setPassword(passwordEncoder.encode(rawPassword));
            owner = userRepository.save(owner);

        }
        //Create a new Franchise for the new Owner
        Franchise franchise = getFranchise(request, owner);

        Franchise saved = franchiseRepository.save(franchise);

        //Build Response Dto
        CreateFranchiseResponseDto response = new CreateFranchiseResponseDto();
        response.setFranchiseId(owner.getId());
        response.setEmail(owner.getEmail());

        if (rawPassword != null) {
            response.setTemporaryPassword(rawPassword);
        } else {
            response.setTemporaryPassword("Already registered");
        }

        response.setMessage("Franchise created successfully");

        return response;

    }

    private static Franchise getFranchise(CreateFranchiseRequestDto request, User owner) {
        Franchise franchise = new Franchise();
        franchise.setOutletName(request.getOutletName());
        franchise.setOwner(owner);     //super important
        franchise.setEmail(request.getEmail());
        franchise.setMobile(request.getMobile());
        franchise.setAddress(request.getAddress());
        franchise.setCity(request.getCity());
        franchise.setState(request.getState());
        franchise.setValidFrom(request.getValidFrom());
        franchise.setValidTo(request.getValidTo());
        return franchise;
    }

    private String generatePassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

}
