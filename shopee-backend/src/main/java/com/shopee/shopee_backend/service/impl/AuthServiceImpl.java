package com.shopee.shopee_backend.service.impl;

import com.shopee.shopee_backend.config.JwtUtil;
import com.shopee.shopee_backend.dto.*;
import com.shopee.shopee_backend.entity.User;
import com.shopee.shopee_backend.exception.ApiException;
import com.shopee.shopee_backend.repository.UserRepository;
import com.shopee.shopee_backend.service.AuthService;
import com.shopee.shopee_backend.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCK_DURATION_MINUTES = 30;
    private static final int OTP_EXPIRY_MINUTES = 15;

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    @Transactional
    public LoginResponseDto login(LoginRequestDto request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ApiException("Invalid credentials", HttpStatus.UNAUTHORIZED));

        if (!user.isActive()) {
            throw new ApiException("Your account has been suspended. Please contact support.", HttpStatus.FORBIDDEN);
        }

        // Auto-unlock if lock has expired
        if (user.isAccountLocked() && user.getLockExpiry() != null
                && LocalDateTime.now().isAfter(user.getLockExpiry())) {
            user.setAccountLocked(false);
            user.setFailedLoginAttempts(0);
            user.setLockExpiry(null);
        }

        if (user.isAccountLocked()) {
            throw new ApiException(
                    "Account locked. Try again after " + user.getLockExpiry().toString(),
                    HttpStatus.LOCKED);
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (BadCredentialsException | DisabledException ex) {
            int attempts = user.getFailedLoginAttempts() + 1;
            user.setFailedLoginAttempts(attempts);
            if (attempts >= MAX_FAILED_ATTEMPTS) {
                user.setAccountLocked(true);
                user.setLockExpiry(LocalDateTime.now().plusMinutes(LOCK_DURATION_MINUTES));
                userRepository.save(user);
                throw new ApiException(
                        "Too many failed attempts. Account locked for " + LOCK_DURATION_MINUTES + " minutes.",
                        HttpStatus.LOCKED);
            }
            userRepository.save(user);
            throw new ApiException("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }

        // Reset on success
        user.setFailedLoginAttempts(0);
        user.setAccountLocked(false);
        user.setLockExpiry(null);

        String accessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getRole(), user.getUserId());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return new LoginResponseDto(accessToken, refreshToken, user.getRole(),
                user.getUserId(), user.getEmail(), user.getName());
    }

    @Override
    @Transactional
    public Map<String, String> refresh(RefreshTokenRequestDto request) {
        String token = request.getRefreshToken();

        String email;
        try {
            email = jwtUtil.extractEmail(token);
        } catch (Exception e) {
            throw new ApiException("Invalid refresh token", HttpStatus.UNAUTHORIZED);
        }

        if (jwtUtil.isTokenExpired(token)) {
            throw new ApiException("Refresh token expired", HttpStatus.UNAUTHORIZED);
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.UNAUTHORIZED));

        if (!token.equals(user.getRefreshToken())) {
            throw new ApiException("Refresh token mismatch", HttpStatus.UNAUTHORIZED);
        }

        String newAccess = jwtUtil.generateAccessToken(user.getEmail(), user.getRole(), user.getUserId());
        String newRefresh = jwtUtil.generateRefreshToken(user.getEmail());
        user.setRefreshToken(newRefresh);
        userRepository.save(user);

        return Map.of("accessToken", newAccess, "refreshToken", newRefresh);
    }

    @Override
    @Transactional
    public void logout(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            user.setRefreshToken(null);
            userRepository.save(user);
        });
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequestDto request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ApiException("No account found with that email", HttpStatus.NOT_FOUND));

        String otp = generateOtp();
        user.setResetOtp(passwordEncoder.encode(otp));
        user.setResetOtpExpiry(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES));
        userRepository.save(user);

        emailService.sendOtpEmail(user.getEmail(), otp);
    }

    @Override
    @Transactional(readOnly = true)
    public void verifyOtp(String email, String otp) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));
        validateOtp(user, otp);
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequestDto request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));

        validateOtp(user, request.getOtp());

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetOtp(null);
        user.setResetOtpExpiry(null);
        user.setRefreshToken(null); // invalidate all sessions on password change
        userRepository.save(user);
    }

    // ---- helpers ----

    private void validateOtp(User user, String rawOtp) {
        if (user.getResetOtp() == null || user.getResetOtpExpiry() == null) {
            throw new ApiException("No OTP requested", HttpStatus.BAD_REQUEST);
        }
        if (LocalDateTime.now().isAfter(user.getResetOtpExpiry())) {
            throw new ApiException("OTP has expired", HttpStatus.BAD_REQUEST);
        }
        if (!passwordEncoder.matches(rawOtp, user.getResetOtp())) {
            throw new ApiException("Invalid OTP", HttpStatus.BAD_REQUEST);
        }
    }

    private String generateOtp() {
        SecureRandom rng = new SecureRandom();
        int value = rng.nextInt(1_000_000);
        return String.format("%06d", value);
    }
}
