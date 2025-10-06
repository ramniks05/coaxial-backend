package com.coaxial.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.coaxial.dto.LoginRequest;
import com.coaxial.dto.LoginResponse;
import com.coaxial.dto.RegistrationRequest;
import com.coaxial.dto.RegistrationResponse;
import com.coaxial.entity.User;
import com.coaxial.entity.UserRole;
import com.coaxial.exception.InvalidCredentialsException;
import com.coaxial.exception.UserDisabledException;
import com.coaxial.exception.UserNotFoundException;
import com.coaxial.security.JwtUtils;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserService userService;

    public LoginResponse authenticate(LoginRequest loginRequest) {
        // First, check if username exists
        User user = userService.getUserByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new UserNotFoundException("Username '" + loginRequest.getUsername() + "' not found"));

        // Check if user account is enabled
        if (!user.getEnabled()) {
            throw new UserDisabledException("Account is disabled. Please contact administrator.");
        }

        try {
            // Attempt authentication
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // Generate new session ID and invalidate previous session
            String newSessionId = UUID.randomUUID().toString();
            user.setCurrentSessionId(newSessionId);
            user.setLastLoginAt(LocalDateTime.now());
            userService.updateUser(user);

            // Generate token
            String token = jwtUtils.generateJwtToken(userDetails);

            return new LoginResponse(token, user);

        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            throw new InvalidCredentialsException("Invalid password for username '" + loginRequest.getUsername() + "'");
        } catch (org.springframework.security.authentication.DisabledException e) {
            throw new UserDisabledException("Account is disabled. Please contact administrator.");
        } catch (Exception e) {
            throw new InvalidCredentialsException("Authentication failed: " + e.getMessage());
        }
    }


    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return userService.getUserByUsername(userDetails.getUsername())
                    .orElse(null);
        }
        return null;
    }

    public void logoutFromAllDevices(String username) {
        User user = userService.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Invalidate current session by setting session ID to null
        user.setCurrentSessionId(null);
        userService.updateUser(user);
    }

    public void logoutFromAllDevices() {
        User currentUser = getCurrentUser();
        if (currentUser != null) {
            logoutFromAllDevices(currentUser.getUsername());
        }
    }

    public RegistrationResponse registerUser(RegistrationRequest registrationRequest) {
        try {
            // Check if username already exists
            if (userService.existsByUsername(registrationRequest.getUsername())) {
                return new RegistrationResponse(false, "Username already exists");
            }

            // Check if email already exists
            if (userService.existsByEmail(registrationRequest.getEmail())) {
                return new RegistrationResponse(false, "Email already exists");
            }

            // Convert role string to UserRole enum
            UserRole userRole;
            try {
                userRole = UserRole.valueOf(registrationRequest.getRole().toUpperCase());
            } catch (IllegalArgumentException e) {
                return new RegistrationResponse(false, "Invalid role. Must be ADMIN, INSTRUCTOR, or STUDENT");
            }

            // Create new user
            User newUser = new User();
            newUser.setUsername(registrationRequest.getUsername());
            newUser.setEmail(registrationRequest.getEmail());
            newUser.setPassword(registrationRequest.getPassword());
            newUser.setFirstName(registrationRequest.getFirstName());
            newUser.setLastName(registrationRequest.getLastName());
            newUser.setRole(userRole);
            newUser.setEnabled(true);

            // Save user (password will be encoded automatically in UserService)
            User savedUser = userService.createUser(newUser);

            return new RegistrationResponse(true, "User registered successfully", savedUser);

        } catch (Exception e) {
            return new RegistrationResponse(false, "Registration failed: " + e.getMessage());
        }
    }
}
