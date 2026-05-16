package com.shopee.shopee_backend.service.impl;

import com.shopee.shopee_backend.dto.CreateStaffRequestDto;
import com.shopee.shopee_backend.dto.StaffDto;
import com.shopee.shopee_backend.entity.Franchise;
import com.shopee.shopee_backend.entity.User;
import com.shopee.shopee_backend.exception.ApiException;
import com.shopee.shopee_backend.exception.ResourceNotFoundException;
import com.shopee.shopee_backend.repository.FranchiseRepository;
import com.shopee.shopee_backend.repository.UserRepository;
import com.shopee.shopee_backend.service.EmailService;
import com.shopee.shopee_backend.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StaffServiceImpl implements StaffService {

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#";

    private final UserRepository userRepository;
    private final FranchiseRepository franchiseRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    @Transactional(readOnly = true)
    public List<StaffDto> getStaffByFranchise(Long franchiseId) {
        return userRepository.findAllByRoleAndAssignedFranchiseFranchiseId("STAFF", franchiseId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public StaffDto createStaff(Long franchiseId, CreateStaffRequestDto request) {
        Franchise franchise = franchiseRepository.findById(franchiseId)
                .orElseThrow(() -> new ResourceNotFoundException("Franchise not found: " + franchiseId));

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ApiException("Email '" + request.getEmail() + "' is already registered", HttpStatus.CONFLICT);
        }

        String tempPassword = generatePassword(12);

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setMobile(request.getMobile());
        user.setPassword(passwordEncoder.encode(tempPassword));
        user.setRole("STAFF");
        user.setActive(true);
        user.setAssignedFranchise(franchise);

        userRepository.save(user);

        emailService.sendWelcomeEmail(user.getEmail(), user.getName(), tempPassword);

        return toDto(user);
    }

    @Override
    @Transactional
    public void removeStaff(Long franchiseId, Long staffId) {
        User user = userRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff member not found: " + staffId));
        if (!"STAFF".equals(user.getRole())) {
            throw new ApiException("User is not a staff member", HttpStatus.BAD_REQUEST);
        }
        if (user.getAssignedFranchise() == null
                || !user.getAssignedFranchise().getFranchiseId().equals(franchiseId)) {
            throw new ApiException("Staff does not belong to franchise " + franchiseId, HttpStatus.FORBIDDEN);
        }
        user.setActive(false);
        user.setAssignedFranchise(null);
        userRepository.save(user);
    }

    // ---- helpers ----

    private String generatePassword(int length) {
        SecureRandom rng = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARS.charAt(rng.nextInt(CHARS.length())));
        }
        return sb.toString();
    }

    private StaffDto toDto(User u) {
        Long franchiseId = u.getAssignedFranchise() != null ? u.getAssignedFranchise().getFranchiseId() : null;
        String franchiseName = u.getAssignedFranchise() != null ? u.getAssignedFranchise().getOutletName() : null;
        return new StaffDto(u.getUserId(), u.getName(), u.getEmail(), u.getMobile(),
                u.isActive(), franchiseId, franchiseName);
    }
}
