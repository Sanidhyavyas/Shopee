package com.shopee.shopee_backend.service.impl;

import com.shopee.shopee_backend.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from:noreply@shopee.com}")
    private String fromAddress;

    @Async
    @Override
    public void sendOtpEmail(String to, String otp) {
        try {
            String subject = "Password Reset OTP";
            String body = "<h3>Password Reset</h3>"
                    + "<p>Your OTP for password reset is: <strong>" + otp + "</strong></p>"
                    + "<p>This OTP is valid for 15 minutes.</p>"
                    + "<p>If you did not request this, please ignore this email.</p>";
            send(to, subject, body);
        } catch (Exception e) {
            log.error("Failed to send OTP email to {}: {}", to, e.getMessage());
        }
    }

    @Async
    @Override
    public void sendWelcomeEmail(String to, String name, String temporaryPassword) {
        try {
            String subject = "Welcome to Shopee — Your Account Details";
            String body = "<h3>Welcome, " + name + "!</h3>"
                    + "<p>Your staff account has been created.</p>"
                    + "<p><strong>Email:</strong> " + to + "</p>"
                    + "<p><strong>Temporary Password:</strong> " + temporaryPassword + "</p>"
                    + "<p>Please log in and change your password immediately.</p>";
            send(to, subject, body);
        } catch (Exception e) {
            log.error("Failed to send welcome email to {}: {}", to, e.getMessage());
        }
    }

    @Async
    @Override
    public void sendLowStockAlert(String to, String productName, String franchiseName, int currentStock) {
        try {
            String subject = "Low Stock Alert — " + franchiseName;
            String body = "<h3>Low Stock Warning</h3>"
                    + "<p>Product <strong>" + productName + "</strong> at franchise <strong>"
                    + franchiseName + "</strong> is running low.</p>"
                    + "<p>Current stock: <strong>" + currentStock + "</strong></p>"
                    + "<p>Please restock at the earliest.</p>";
            send(to, subject, body);
        } catch (Exception e) {
            log.error("Failed to send low-stock alert to {}: {}", to, e.getMessage());
        }
    }

    private void send(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
        helper.setFrom(fromAddress);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        mailSender.send(message);
    }
}
