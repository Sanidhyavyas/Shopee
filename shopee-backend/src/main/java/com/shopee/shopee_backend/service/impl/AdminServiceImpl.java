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
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final FranchiseRepository franchiseRepository;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public List<FranchiseDto> getAllFranchise() {
        return franchiseRepository.findAll()
                .stream()
                .map(franchise -> {
                    FranchiseDto dto = modelMapper.map(franchise, FranchiseDto.class);
                    dto.setOwnerEmail(franchise.getOwner().getEmail());
                    dto.setOwnerMobile(franchise.getOwner().getMobile());
                    return dto;
                })
                .toList();
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .toList();
    }

    @Override
    public CreateFranchiseResponseDto createFranchise(CreateFranchiseRequestDto request) {

        User owner = userRepository.findByEmail(request.getEmail()).orElse(null);
        String rawPassword = null;

        if (owner == null) {
            owner = new User();
            owner.setEmail(request.getEmail());
            owner.setMobile(request.getMobile());
            owner.setRole("FRANCHISE_ADMIN");
            owner.setActive(true);

            rawPassword = generatePassword();
            owner.setPassword(passwordEncoder.encode(rawPassword));

            owner = userRepository.save(owner);
        }

        Franchise franchise = mapToFranchiseEntity(request, owner);
        Franchise saved = franchiseRepository.save(franchise);

        CreateFranchiseResponseDto response = new CreateFranchiseResponseDto();
        response.setFranchiseId(saved.getFranchiseId());
        response.setEmail(owner.getEmail());

        if (rawPassword != null) {
            response.setTemporaryPassword(rawPassword);
        } else {
            response.setTemporaryPassword("Already registered");
        }

        response.setMessage("Franchise created successfully");
        return response;
    }

    private Franchise mapToFranchiseEntity(CreateFranchiseRequestDto request, User owner) {
        Franchise franchise = new Franchise();
        franchise.setOutletName(request.getOutletName());
        franchise.setOwner(owner);
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
