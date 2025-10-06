package com.coaxial.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.coaxial.dto.LoginRequest;
import com.coaxial.dto.LoginResponse;
import com.coaxial.entity.User;
import com.coaxial.exception.InvalidCredentialsException;
import com.coaxial.exception.UserDisabledException;
import com.coaxial.repository.UserRepository;
import com.coaxial.security.CustomUserDetailsService;
import com.coaxial.security.JwtUtils;

@Service
public class AuthenticationService {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    @Autowired
    private CustomUserDetailsService userDetailsService;
    
    @Autowired
    private UserRepository userRepository;
    
    public LoginResponse authenticate(LoginRequest loginRequest) {
        try {
            // Attempt authentication
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );
            
            // Load user details and generate token
            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
            String token = jwtUtils.generateJwtToken(userDetails);
            
            // Update last login time
            Optional<User> userOpt = userRepository.findByUsername(loginRequest.getUsername());
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                user.setLastLoginAt(LocalDateTime.now());
                userRepository.save(user);
                LoginResponse response = new LoginResponse(token, user, "Login successful");
                response.setType("Bearer");
                return response;
            }
            
            LoginResponse response = new LoginResponse(token, null, "Login successful but user details not found");
            response.setType("Bearer");
            return response;
            
        } catch (DisabledException e) {
            throw new UserDisabledException("Your account has been disabled");
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Invalid username or password");
        }
    }
    
    public void logout(String username) {
        // Since we're using stateless JWT authentication, we don't need to do anything here
        // The client should remove the JWT token from their storage
        // Future enhancement: Add token to a blacklist if needed
    }
}
