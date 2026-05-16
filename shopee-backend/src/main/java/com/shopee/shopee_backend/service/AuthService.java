package com.shopee.shopee_backend.service;

import com.shopee.shopee_backend.dto.ForgotPasswordRequestDto;
import com.shopee.shopee_backend.dto.LoginRequestDto;
import com.shopee.shopee_backend.dto.LoginResponseDto;
import com.shopee.shopee_backend.dto.RefreshTokenRequestDto;
import com.shopee.shopee_backend.dto.ResetPasswordRequestDto;

import java.util.Map;

public interface AuthService {

    LoginResponseDto login(LoginRequestDto request);

    Map<String, String> refresh(RefreshTokenRequestDto request);

    void logout(String email);

    /** Generates a 6-digit OTP, stores it hashed, and emails it to the user. */
    void forgotPassword(ForgotPasswordRequestDto request);

    /** Validates the OTP without consuming it (used for a two-step reset flow). */
    void verifyOtp(String email, String otp);

    /** Validates the OTP and sets the new password in one step. */
    void resetPassword(ResetPasswordRequestDto request);
}
