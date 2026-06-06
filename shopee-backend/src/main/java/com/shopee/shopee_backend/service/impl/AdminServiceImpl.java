package com.shopee.shopee_backend.service.impl;

import com.shopee.shopee_backend.dto.*;
import com.shopee.shopee_backend.entity.Franchise;
import com.shopee.shopee_backend.entity.OrderStatus;
import com.shopee.shopee_backend.entity.User;
import com.shopee.shopee_backend.exception.ResourceNotFoundException;
import com.shopee.shopee_backend.repository.FranchiseRepository;
import com.shopee.shopee_backend.repository.OrderRepository;
import com.shopee.shopee_backend.repository.ProductRepository;
import com.shopee.shopee_backend.repository.UserRepository;
import com.shopee.shopee_backend.service.AdminService;
import com.shopee.shopee_backend.service.AuditLogService;
import com.shopee.shopee_backend.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final FranchiseRepository franchiseRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final AuditLogService auditLogService;

    @Override
    @Transactional(readOnly = true)
    @Cacheable("franchises")
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
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .toList();
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "franchises", allEntries = true),
        @CacheEvict(value = "dashboardStats", allEntries = true)
    })
    public CreateFranchiseResponseDto createFranchise(CreateFranchiseRequestDto request) {
        User owner = userRepository.findByEmail(request.getEmail()).orElse(null);
        String rawPassword = null;

        if (owner == null) {
            owner = new User();
            owner.setEmail(request.getEmail());
            owner.setMobile(request.getMobile());
            owner.setName(request.getOwnerName());
            owner.setRole("FRANCHISE_ADMIN");
            owner.setActive(true);

            rawPassword = generatePassword();
            owner.setPassword(passwordEncoder.encode(rawPassword));

            owner = userRepository.save(owner);
        } else {
            if (request.getOwnerName() != null && (owner.getName() == null || owner.getName().isBlank())) {
                owner.setName(request.getOwnerName());
                userRepository.save(owner);
            }
        }

        Franchise franchise = mapToFranchiseEntity(request, owner);
        Franchise saved = franchiseRepository.save(franchise);

        if (rawPassword != null) {
            emailService.sendWelcomeEmail(owner.getEmail(), owner.getName(), rawPassword);
        }

        CreateFranchiseResponseDto response = new CreateFranchiseResponseDto();
        response.setFranchiseId(saved.getFranchiseId());
        response.setEmail(owner.getEmail());
        response.setTemporaryPassword(rawPassword != null ? rawPassword : "Already registered");
        response.setMessage("Franchise created successfully");

        FranchiseDto franchiseDto = modelMapper.map(saved, FranchiseDto.class);
        franchiseDto.setOwnerEmail(saved.getOwner().getEmail());
        franchiseDto.setOwnerMobile(saved.getOwner().getMobile());
        auditLogService.log("CREATE_FRANCHISE", "Franchise",
                String.valueOf(saved.getFranchiseId()), null, franchiseDto);

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable("dashboardStats")
    public DashboardStatsDto getDashboardStats() {
        long totalFranchises = franchiseRepository.count();
        long activeFranchises = franchiseRepository.findAllByActiveTrue().size();
        long totalUsers = userRepository.count();
        long totalOrders = orderRepository.count();
        long pendingOrders = orderRepository.countByStatus(OrderStatus.PENDING);
        BigDecimal revenue = orderRepository.sumTotalRevenue();
        long lowStock = productRepository.findAllLowStock().size();

        return new DashboardStatsDto(totalFranchises, activeFranchises, totalUsers,
                totalOrders, pendingOrders, revenue, lowStock);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LowStockAlertDto> getLowStockAlerts() {
        return productRepository.findAllLowStock().stream()
                .map(p -> new LowStockAlertDto(
                        p.getProductId(), p.getName(), p.getSku(),
                        p.getStockQuantity(), p.getMinStockAlert(),
                        p.getFranchise().getFranchiseId(),
                        p.getFranchise().getOutletName(),
                        p.getFranchise().getCity()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @CacheEvict(value = "franchises", allEntries = true)
    public FranchiseDto updateFranchise(Long franchiseId, UpdateFranchiseRequestDto request) {
        Franchise franchise = franchiseRepository.findById(franchiseId)
                .orElseThrow(() -> new ResourceNotFoundException("Franchise not found: " + franchiseId));

        FranchiseDto oldDto = modelMapper.map(franchise, FranchiseDto.class);
        oldDto.setOwnerEmail(franchise.getOwner().getEmail());
        oldDto.setOwnerMobile(franchise.getOwner().getMobile());

        if (request.getOutletName() != null) franchise.setOutletName(request.getOutletName());
        if (request.getAddress() != null) franchise.setAddress(request.getAddress());
        if (request.getCity() != null) franchise.setCity(request.getCity());
        if (request.getState() != null) franchise.setState(request.getState());
        if (request.getPincode() != null) franchise.setPincode(request.getPincode());
        if (request.getPhone() != null) franchise.setPhone(request.getPhone());
        if (request.getValidFrom() != null) franchise.setValidFrom(request.getValidFrom());
        if (request.getValidTo() != null) franchise.setValidTo(request.getValidTo());
        if (request.getSubscriptionPlan() != null) franchise.setSubscriptionPlan(request.getSubscriptionPlan());

        Franchise saved = franchiseRepository.save(franchise);
        FranchiseDto dto = modelMapper.map(saved, FranchiseDto.class);
        dto.setOwnerEmail(saved.getOwner().getEmail());
        dto.setOwnerMobile(saved.getOwner().getMobile());

        auditLogService.log("UPDATE_FRANCHISE", "Franchise",
                String.valueOf(franchiseId), oldDto, dto);
        return dto;
    }

    @Override
    @Transactional
    public FranchiseDto toggleFranchiseStatus(Long franchiseId, boolean active) {
        Franchise franchise = franchiseRepository.findById(franchiseId)
                .orElseThrow(() -> new ResourceNotFoundException("Franchise not found: " + franchiseId));
        franchise.setActive(active);
        Franchise saved = franchiseRepository.save(franchise);
        FranchiseDto dto = modelMapper.map(saved, FranchiseDto.class);
        dto.setOwnerEmail(saved.getOwner().getEmail());
        dto.setOwnerMobile(saved.getOwner().getMobile());
        return dto;
    }

    @Override
    @Transactional
    public UserDto updateUserStatus(Long userId, UpdateUserStatusRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        boolean oldStatus = user.isActive();
        user.setActive(request.getActive());
        UserDto updated = modelMapper.map(userRepository.save(user), UserDto.class);
        auditLogService.log("UPDATE_USER_STATUS", "User",
                String.valueOf(userId), oldStatus, request.getActive());
        return updated;
    }

    // ---- helpers ----

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

