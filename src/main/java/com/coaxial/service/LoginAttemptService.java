package com.coaxial.service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.coaxial.entity.User;
import com.coaxial.exception.AccountLockedException;
import com.coaxial.exception.TooManyAttemptsException;
import com.coaxial.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class LoginAttemptService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuditLogService auditLogService;


    @Value("${security.login.max-attempts:5}")
    private int maxAttempts;

    @Value("${security.login.lock-duration-minutes:15}")
    private int lockDurationMinutes;

    @Value("${security.login.ip-lock-duration-minutes:30}")
    private int ipLockDurationMinutes;

    // In-memory storage for IP-based rate limiting (fallback when Redis is not available)
    private final ConcurrentHashMap<String, LoginAttempt> ipAttempts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, LoginAttempt> usernameAttempts = new ConcurrentHashMap<>();

    public static class LoginAttempt {
        private int attempts = 0;
        private LocalDateTime firstAttempt;
        private LocalDateTime lastAttempt;
        private LocalDateTime lockedUntil;

        public LoginAttempt() {
            this.firstAttempt = LocalDateTime.now();
            this.lastAttempt = LocalDateTime.now();
        }

        public void incrementAttempts() {
            this.attempts++;
            this.lastAttempt = LocalDateTime.now();
        }

        public void resetAttempts() {
            this.attempts = 0;
            this.firstAttempt = LocalDateTime.now();
            this.lastAttempt = LocalDateTime.now();
            this.lockedUntil = null;
        }

        public void lock(int durationMinutes) {
            this.lockedUntil = LocalDateTime.now().plusMinutes(durationMinutes);
        }

        public boolean isLocked() {
            return lockedUntil != null && lockedUntil.isAfter(LocalDateTime.now());
        }

        public boolean isExpired(int durationMinutes) {
            return lastAttempt.plusMinutes(durationMinutes).isBefore(LocalDateTime.now());
        }

        // Getters and Setters
        public int getAttempts() { return attempts; }
        public LocalDateTime getFirstAttempt() { return firstAttempt; }
        public LocalDateTime getLastAttempt() { return lastAttempt; }
        public LocalDateTime getLockedUntil() { return lockedUntil; }
        public void setAttempts(int attempts) { this.attempts = attempts; }
        public void setFirstAttempt(LocalDateTime firstAttempt) { this.firstAttempt = firstAttempt; }
        public void setLastAttempt(LocalDateTime lastAttempt) { this.lastAttempt = lastAttempt; }
        public void setLockedUntil(LocalDateTime lockedUntil) { this.lockedUntil = lockedUntil; }
    }

    public void checkLoginAttempts(String username, HttpServletRequest request) {
        String ipAddress = getClientIpAddress(request);

        // Check IP-based rate limiting
        checkIpRateLimit(ipAddress, request);

        // Check username-based rate limiting
        checkUsernameRateLimit(username, request);
    }

    private void checkIpRateLimit(String ipAddress, HttpServletRequest request) {
        LoginAttempt attempt = ipAttempts.get(ipAddress);
        
        if (attempt != null) {
            if (attempt.isLocked()) {
                auditLogService.logAccountLocked(ipAddress, "IP address locked due to too many failed attempts", request);
                throw new TooManyAttemptsException("IP address has been temporarily blocked due to too many failed login attempts. Please try again later.");
            }
            
            if (attempt.isExpired(ipLockDurationMinutes)) {
                attempt.resetAttempts();
            }
        }
    }

    private void checkUsernameRateLimit(String username, HttpServletRequest request) {
        LoginAttempt attempt = usernameAttempts.get(username);
        
        if (attempt != null) {
            if (attempt.isLocked()) {
                auditLogService.logAccountLocked(username, "Account locked due to too many failed attempts", request);
                throw new AccountLockedException("Account has been temporarily locked due to too many failed login attempts. Please try again later.");
            }
            
            if (attempt.isExpired(lockDurationMinutes)) {
                attempt.resetAttempts();
            }
        }
    }

    public void recordFailedAttempt(String username, HttpServletRequest request) {
        String ipAddress = getClientIpAddress(request);

        // Record failed attempt for IP (in-memory storage)
        LoginAttempt ipAttempt = ipAttempts.computeIfAbsent(ipAddress, k -> new LoginAttempt());
        ipAttempt.incrementAttempts();

        if (ipAttempt.getAttempts() >= maxAttempts) {
            ipAttempt.lock(ipLockDurationMinutes);
            auditLogService.logAccountLocked(ipAddress, "IP locked after " + maxAttempts + " failed attempts", request);
        }

        // Record failed attempt for username (in-memory storage)
        LoginAttempt usernameAttempt = usernameAttempts.computeIfAbsent(username, k -> new LoginAttempt());
        usernameAttempt.incrementAttempts();

        if (usernameAttempt.getAttempts() >= maxAttempts) {
            usernameAttempt.lock(lockDurationMinutes);
            auditLogService.logAccountLocked(username, "Account locked after " + maxAttempts + " failed attempts", request);
            
            // Also lock the user account in database
            lockUserAccount(username);
        }
    }

    public void recordSuccessfulAttempt(String username, HttpServletRequest request) {
        String ipAddress = getClientIpAddress(request);

        // Reset attempts for successful login (in-memory storage)
        LoginAttempt ipAttempt = ipAttempts.get(ipAddress);
        if (ipAttempt != null) {
            ipAttempt.resetAttempts();
        }

        LoginAttempt usernameAttempt = usernameAttempts.get(username);
        if (usernameAttempt != null) {
            usernameAttempt.resetAttempts();
        }

        // Reset user account lock in database
        unlockUserAccount(username);
    }

    private void lockUserAccount(String username) {
        try {
            User user = userRepository.findByUsername(username).orElse(null);
            if (user != null) {
                user.setAccountLockedUntil(LocalDateTime.now().plusMinutes(lockDurationMinutes));
                userRepository.save(user);
            }
        } catch (Exception e) {
            // Log error but don't fail the authentication process
            System.err.println("Error locking user account: " + e.getMessage());
        }
    }

    private void unlockUserAccount(String username) {
        try {
            User user = userRepository.findByUsername(username).orElse(null);
            if (user != null) {
                user.resetFailedAttempts();
                userRepository.save(user);
            }
        } catch (Exception e) {
            // Log error but don't fail the authentication process
            System.err.println("Error unlocking user account: " + e.getMessage());
        }
    }

    public boolean isAccountLocked(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        return user != null && user.isAccountLocked();
    }

    public int getRemainingAttempts(String username) {
        LoginAttempt attempt = usernameAttempts.get(username);
        if (attempt == null) {
            return maxAttempts;
        }
        return Math.max(0, maxAttempts - attempt.getAttempts());
    }

    public int getRemainingAttemptsByIp(String ipAddress) {
        LoginAttempt attempt = ipAttempts.get(ipAddress);
        if (attempt == null) {
            return maxAttempts;
        }
        return Math.max(0, maxAttempts - attempt.getAttempts());
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
