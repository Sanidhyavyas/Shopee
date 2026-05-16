package com.shopee.shopee_backend.service;

public interface EmailService {

    void sendOtpEmail(String to, String otp);

    void sendWelcomeEmail(String to, String name, String temporaryPassword);

    void sendLowStockAlert(String to, String productName, String franchiseName, int currentStock);
}
