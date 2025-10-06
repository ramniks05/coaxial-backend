package com.coaxial.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.coaxial.dto.ForgotPasswordRequest;
import com.coaxial.dto.ResetPasswordRequest;
import com.coaxial.entity.PasswordResetToken;
import com.coaxial.entity.User;
import com.coaxial.exception.InvalidTokenException;
import com.coaxial.exception.TokenExpiredException;
import com.coaxial.repository.PasswordResetTokenRepository;
import com.coaxial.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class PasswordResetService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuditLogService auditLogService;

    @Value("${app.password-reset.expiration-hours:24}")
    private int tokenExpirationHours;

    @Value("${app.password-reset.base-url:http://localhost:3000}")
    private String baseUrl;

    @Value("${app.password-reset.from-email:noreply@coaxial.com}")
    private String fromEmail;

    public void requestPasswordReset(ForgotPasswordRequest request, HttpServletRequest httpRequest) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            // Invalidate any existing tokens for this user
            tokenRepository.deleteAll(tokenRepository.findAll().stream()
                .filter(t -> t.getUser().getId().equals(user.getId()))
                .toList());
            
            // Create new password reset token
            String token = UUID.randomUUID().toString();
            LocalDateTime expiresAt = LocalDateTime.now().plusHours(tokenExpirationHours);
            
            PasswordResetToken resetToken = new PasswordResetToken(token, user, expiresAt);
            tokenRepository.save(resetToken);
            
            // Send email
            sendPasswordResetEmail(user, token);
            
            // Log the password reset request
            auditLogService.logPasswordResetRequest(user.getEmail(), httpRequest);
        }
        // Always return success to prevent email enumeration attacks
    }

    public void resetPassword(ResetPasswordRequest request, HttpServletRequest httpRequest) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByTokenAndUsedFalse(request.getToken());
        
        if (!tokenOpt.isPresent()) {
            throw new InvalidTokenException("Invalid or already used password reset token");
        }
        
        PasswordResetToken token = tokenOpt.get();
        
        if (token.isExpired()) {
            throw new TokenExpiredException("Password reset token has expired");
        }
        
        // Update user password
        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        
        // Mark token as used
        token.setUsed(true);
        tokenRepository.save(token);
        
        // Log the successful password reset
        auditLogService.logPasswordResetSuccess(user, httpRequest);
    }

    private void sendPasswordResetEmail(User user, String token) {
        if (mailSender == null) {
            System.out.println("Mail sender not configured. Password reset email not sent to: " + user.getEmail());
            System.out.println("Reset token: " + token);
            return;
        }
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(user.getEmail());
            message.setSubject("Password Reset Request - Coaxial LMS");
            
            String resetUrl = baseUrl + "/reset-password?token=" + token;
            String emailBody = buildEmailBody(user.getFullName(), resetUrl);
            
            message.setText(emailBody);
            mailSender.send(message);
        } catch (Exception e) {
            // Log error but don't fail the password reset process
            System.err.println("Error sending password reset email: " + e.getMessage());
        }
    }

    private String buildEmailBody(String userName, String resetUrl) {
        return String.format("""
            Hello %s,
            
            You have requested to reset your password for your Coaxial LMS account.
            
            To reset your password, please click on the following link:
            %s
            
            This link will expire in %d hours.
            
            If you did not request this password reset, please ignore this email.
            
            Best regards,
            Coaxial LMS Team
            """, userName, resetUrl, tokenExpirationHours);
    }
}
