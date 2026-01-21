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

/**
 * Service implementation for Administrative tasks.
 * Handles user management and franchise onboarding.
 */
@RequiredArgsConstructor
@Service
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final FranchiseRepository franchiseRepository;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * Retrieves all registered franchises and maps them to DTOs for the UI.
     */
    @Override
    public List<FranchiseDto> getAllFranchise(){
        return franchiseRepository.findAll()
                .stream()
                .map(franchise -> modelMapper.map(franchise, FranchiseDto.class))
                .toList();
    }

    /**
     * Retrieves all users system-wide.
     */
    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .toList();
    }

    /**
     * Onboards a new franchise.
     * Logic: If the owner's email doesn't exist, it creates a new User with a random password.
     */
    @Override
    public CreateFranchiseResponseDto createFranchise(CreateFranchiseRequestDto request) {

        // 1. Check if an account already exists for this email
        User owner = userRepository.findByEmail(request.getEmail()).orElse(null);
        String rawPassword = null;

        // 2. Provision a new User account if one doesn't exist
        if(owner == null){
            owner = new User();
            owner.setEmail(request.getEmail());
            owner.setRole("FRANCHISE ADMIN");
            owner.setActive(true);

            // Generate and encode a temporary password for the new owner
            rawPassword = generatePassword();
            owner.setPassword(passwordEncoder.encode(rawPassword));

            // Persist the user so we have an ID for the franchise relationship
            owner = userRepository.save(owner);
        }

        // 3. Link the franchise to the User (Owner) and save
        Franchise franchise = mapToFranchiseEntity(request, owner);
        franchiseRepository.save(franchise);

        // 4. Construct the response, including the temporary password only if generated
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

    /**
     * Helper method to manually map the Request Dto to a Franchise Entity.
     * Established the crucial many-to-one relationship with the Owner.
     */
    private static Franchise mapToFranchiseEntity(CreateFranchiseRequestDto request, User owner) {
        Franchise franchise = new Franchise();
        franchise.setOutletName(request.getOutletName());
        franchise.setOwner(owner); // Linking the entity to the User account
        franchise.setEmail(request.getEmail());
        franchise.setMobile(request.getMobile());
        franchise.setAddress(request.getAddress());
        franchise.setCity(request.getCity());
        franchise.setState(request.getState());
        franchise.setValidFrom(request.getValidFrom());
        franchise.setValidTo(request.getValidTo());
        return franchise;
    }

    /**
     * Generates a short, 8-character random string for temporary passwords.
     */
    private String generatePassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}