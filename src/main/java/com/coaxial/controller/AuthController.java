package com.coaxial.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coaxial.dto.ErrorResponse;
import com.coaxial.dto.ForgotPasswordRequest;
import com.coaxial.dto.LoginRequest;
import com.coaxial.dto.LoginResponse;
import com.coaxial.dto.ResetPasswordRequest;
import com.coaxial.dto.TokenRefreshRequest;
import com.coaxial.dto.TokenRefreshResponse;
import com.coaxial.dto.UserRequest;
import com.coaxial.dto.UserResponse;
import com.coaxial.entity.User;
import com.coaxial.exception.AccountLockedException;
import com.coaxial.exception.InvalidCredentialsException;
import com.coaxial.exception.InvalidTokenException;
import com.coaxial.exception.TokenExpiredException;
import com.coaxial.exception.TooManyAttemptsException;
import com.coaxial.exception.UserDisabledException;
import com.coaxial.exception.UserNotFoundException;
import com.coaxial.security.CustomUserDetailsService;
import com.coaxial.security.JwtUtils;
import com.coaxial.service.AuthenticationService;
import com.coaxial.service.LoginAttemptService;
import com.coaxial.service.PasswordResetService;
import com.coaxial.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationService authenticationService;
    
    @Autowired
    private UserService userService;

    @Autowired
    private LoginAttemptService loginAttemptService;

    @Autowired
    private PasswordResetService passwordResetService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Value("${jwt.expiration:86400000}")
    private int jwtExpirationMs;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        try {
            // Check rate limiting before authentication
            loginAttemptService.checkLoginAttempts(loginRequest.getUsername(), request);
            
            // Attempt authentication
            LoginResponse response = authenticationService.authenticate(loginRequest);
            
            // Record successful login attempt
            loginAttemptService.recordSuccessfulAttempt(loginRequest.getUsername(), request);
            
            return ResponseEntity.ok(response);
            
        } catch (TooManyAttemptsException e) {
            ErrorResponse error = new ErrorResponse("TOO_MANY_ATTEMPTS", e.getMessage(), "RATE_LIMIT_EXCEEDED");
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(error);
        } catch (AccountLockedException e) {
            ErrorResponse error = new ErrorResponse("ACCOUNT_LOCKED", e.getMessage(), "ACCOUNT_LOCKED");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        } catch (UserDisabledException e) {
            ErrorResponse error = new ErrorResponse("ACCOUNT_DISABLED", e.getMessage(), "ACCOUNT_DISABLED");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        } catch (InvalidCredentialsException e) {
            // Record failed login attempt
            loginAttemptService.recordFailedAttempt(loginRequest.getUsername(), request);
            
            int remainingAttempts = loginAttemptService.getRemainingAttempts(loginRequest.getUsername());
            ErrorResponse error = new ErrorResponse("INVALID_CREDENTIALS", e.getMessage(), "INVALID_CREDENTIALS");
            error.setDetails(Map.of("remainingAttempts", remainingAttempts));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        } catch (UserNotFoundException e) {
            // Record failed login attempt for non-existent user
            loginAttemptService.recordFailedAttempt(loginRequest.getUsername(), request);
            
            ErrorResponse error = new ErrorResponse("USER_NOT_FOUND", "Invalid username or password", "INVALID_CREDENTIALS");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred", "INTERNAL_ERROR");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody UserRequest userRequest) {
        UserResponse user = userService.createUser(userRequest);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "User registered successfully");
        response.put("user", user);
        
        // Generate JWT token for auto-login after registration
        LoginRequest loginRequest = new LoginRequest(userRequest.getUsername(), userRequest.getPassword());
        try {
            LoginResponse loginResponse = authenticationService.authenticate(loginRequest);
            response.put("token", loginResponse.getToken());
            response.put("type", loginResponse.getType());
        } catch (Exception e) {
            // If authentication fails after registration, just return the user data
            response.put("message", "User registered successfully. Please login to continue.");
        }
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logout successful");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Authentication API is working");
        response.put("status", "OK");
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/create-admin")
    public ResponseEntity<?> createAdmin() {
        try {
            User adminUser = userService.createAdminUser();
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Admin user created successfully");
            response.put("username", adminUser.getUsername());
            response.put("password", "admin123"); // Show the password for testing
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to create admin user");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshRequest request, HttpServletRequest httpRequest) {
        try {
            String token = request.getToken();
            
            // Validate the refresh token
            if (!jwtUtils.validateRefreshToken(token)) {
                ErrorResponse error = new ErrorResponse("INVALID_TOKEN", "Invalid refresh token", "INVALID_TOKEN");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            // Extract username from token
            String username = jwtUtils.getUserNameFromJwtToken(token);
            
            // Load user details
            var userDetails = userDetailsService.loadUserByUsername(username);
            
            // Generate new access token
            String newToken = jwtUtils.generateTokenFromRefreshToken(token, userDetails);
            
            // Get user info for response
            User user = userService.getUserByUsername(username).orElse(null);
            if (user != null) {
                user.setLastLoginIp(getClientIpAddress(httpRequest));
                userService.updateUser(user);
            }
            
            TokenRefreshResponse response = new TokenRefreshResponse(
                newToken, 
                "Token refreshed successfully", 
                jwtExpirationMs / 1000L // Convert to seconds
            );
            
            return ResponseEntity.ok(response);
            
        } catch (InvalidTokenException e) {
            ErrorResponse error = new ErrorResponse("INVALID_TOKEN", e.getMessage(), "INVALID_TOKEN");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        } catch (TokenExpiredException e) {
            ErrorResponse error = new ErrorResponse("TOKEN_EXPIRED", e.getMessage(), "TOKEN_EXPIRED");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("TOKEN_REFRESH_FAILED", "Token refresh failed", "INTERNAL_ERROR");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request, HttpServletRequest httpRequest) {
        try {
            passwordResetService.requestPasswordReset(request, httpRequest);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "If an account with that email exists, a password reset link has been sent.");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("PASSWORD_RESET_FAILED", "Failed to process password reset request", "INTERNAL_ERROR");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request, HttpServletRequest httpRequest) {
        try {
            passwordResetService.resetPassword(request, httpRequest);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Password has been reset successfully");
            return ResponseEntity.ok(response);
            
        } catch (InvalidTokenException e) {
            ErrorResponse error = new ErrorResponse("INVALID_TOKEN", e.getMessage(), "INVALID_TOKEN");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (TokenExpiredException e) {
            ErrorResponse error = new ErrorResponse("TOKEN_EXPIRED", e.getMessage(), "TOKEN_EXPIRED");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("PASSWORD_RESET_FAILED", "Failed to reset password", "INTERNAL_ERROR");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
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
