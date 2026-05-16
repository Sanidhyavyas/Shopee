package com.shopee.shopee_backend.config;

import com.shopee.shopee_backend.entity.User;
import com.shopee.shopee_backend.exception.ApiException;
import com.shopee.shopee_backend.exception.ResourceNotFoundException;
import com.shopee.shopee_backend.repository.FranchiseRepository;
import com.shopee.shopee_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final UserRepository userRepository;
    private final FranchiseRepository franchiseRepository;

    /** Returns the User entity for the currently authenticated principal. */
    @Transactional(readOnly = true)
    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }

    /**
     * Validates that the current user has read/write access to the given franchise.
     * SUPER_ADMIN has unrestricted access.
     * FRANCHISE_ADMIN must own the franchise.
     * STAFF must be assigned to the franchise.
     */
    @Transactional(readOnly = true)
    public void requireFranchiseAccess(Long franchiseId) {
        User user = getCurrentUser();
        if ("SUPER_ADMIN".equals(user.getRole())) return;

        if ("STAFF".equals(user.getRole())) {
            if (user.getAssignedFranchise() == null
                    || !user.getAssignedFranchise().getFranchiseId().equals(franchiseId)) {
                throw new ApiException("Access denied to this franchise", HttpStatus.FORBIDDEN);
            }
        } else if ("FRANCHISE_ADMIN".equals(user.getRole())) {
            if (!franchiseRepository.existsByFranchiseIdAndOwnerUserId(franchiseId, user.getUserId())) {
                throw new ApiException("Access denied to this franchise", HttpStatus.FORBIDDEN);
            }
        } else {
            throw new ApiException("Access denied", HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Like requireFranchiseAccess but additionally restricts STAFF — only FRANCHISE_ADMIN (or SUPER_ADMIN)
     * may call the guarded operation.
     */
    @Transactional(readOnly = true)
    public void requireFranchiseAdminAccess(Long franchiseId) {
        User user = getCurrentUser();
        if ("SUPER_ADMIN".equals(user.getRole())) return;

        if (!"FRANCHISE_ADMIN".equals(user.getRole())) {
            throw new ApiException("Only franchise admins can perform this action", HttpStatus.FORBIDDEN);
        }
        if (!franchiseRepository.existsByFranchiseIdAndOwnerUserId(franchiseId, user.getUserId())) {
            throw new ApiException("Access denied to this franchise", HttpStatus.FORBIDDEN);
        }
    }
}
