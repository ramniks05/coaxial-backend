package com.coaxial.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.coaxial.entity.AuditLog;
import com.coaxial.entity.User;
import com.coaxial.enums.AuditEventType;
import com.coaxial.repository.AuditLogRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    public void logLoginSuccess(User user, HttpServletRequest request) {
        AuditLog auditLog = new AuditLog(
            user.getId(),
            user.getUsername(),
            AuditEventType.LOGIN_SUCCESS,
            "User login successful",
            getClientIpAddress(request),
            request.getHeader("User-Agent"),
            true,
            null
        );
        auditLogRepository.save(auditLog);
    }

    public void logLoginFailure(String username, String failureReason, HttpServletRequest request) {
        AuditLog auditLog = new AuditLog(
            null,
            username,
            AuditEventType.LOGIN_FAILURE,
            "User login failed: " + failureReason,
            getClientIpAddress(request),
            request.getHeader("User-Agent"),
            false,
            failureReason
        );
        auditLogRepository.save(auditLog);
    }

    public void logLogout(User user, HttpServletRequest request) {
        AuditLog auditLog = new AuditLog(
            user.getId(),
            user.getUsername(),
            AuditEventType.LOGOUT,
            "User logout",
            getClientIpAddress(request),
            request.getHeader("User-Agent"),
            true,
            null
        );
        auditLogRepository.save(auditLog);
    }

    public void logTokenRefresh(User user, HttpServletRequest request) {
        AuditLog auditLog = new AuditLog(
            user.getId(),
            user.getUsername(),
            AuditEventType.TOKEN_REFRESH,
            "Token refresh successful",
            getClientIpAddress(request),
            request.getHeader("User-Agent"),
            true,
            null
        );
        auditLogRepository.save(auditLog);
    }

    public void logPasswordResetRequest(String email, HttpServletRequest request) {
        AuditLog auditLog = new AuditLog(
            null,
            email,
            AuditEventType.PASSWORD_RESET_REQUEST,
            "Password reset requested",
            getClientIpAddress(request),
            request.getHeader("User-Agent"),
            true,
            null
        );
        auditLogRepository.save(auditLog);
    }

    public void logPasswordResetSuccess(User user, HttpServletRequest request) {
        AuditLog auditLog = new AuditLog(
            user.getId(),
            user.getUsername(),
            AuditEventType.PASSWORD_RESET_SUCCESS,
            "Password reset successful",
            getClientIpAddress(request),
            request.getHeader("User-Agent"),
            true,
            null
        );
        auditLogRepository.save(auditLog);
    }

    public void logAccountLocked(String username, String reason, HttpServletRequest request) {
        AuditLog auditLog = new AuditLog(
            null,
            username,
            AuditEventType.ACCOUNT_LOCKED,
            "Account locked: " + reason,
            getClientIpAddress(request),
            request.getHeader("User-Agent"),
            false,
            reason
        );
        auditLogRepository.save(auditLog);
    }

    public void logAccountUnlocked(User user, HttpServletRequest request) {
        AuditLog auditLog = new AuditLog(
            user.getId(),
            user.getUsername(),
            AuditEventType.ACCOUNT_UNLOCKED,
            "Account unlocked",
            getClientIpAddress(request),
            request.getHeader("User-Agent"),
            true,
            null
        );
        auditLogRepository.save(auditLog);
    }

    public void logRegistration(User user, HttpServletRequest request) {
        AuditLog auditLog = new AuditLog(
            user.getId(),
            user.getUsername(),
            AuditEventType.REGISTRATION,
            "User registration successful",
            getClientIpAddress(request),
            request.getHeader("User-Agent"),
            true,
            null
        );
        auditLogRepository.save(auditLog);
    }

    public long countFailedLoginAttemptsByIp(String ipAddress, LocalDateTime since) {
        return auditLogRepository.countByIpAddressAndEventTypeSince(ipAddress, AuditEventType.LOGIN_FAILURE, since);
    }

    public long countFailedLoginAttemptsByUsername(String username, LocalDateTime since) {
        return auditLogRepository.countByUsernameAndEventTypeSince(username, AuditEventType.LOGIN_FAILURE, since);
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader == null) {
            return request.getRemoteAddr();
        } else {
            return xForwardedForHeader.split(",")[0];
        }
    }
}
