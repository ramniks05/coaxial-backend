package com.coaxial.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coaxial.dto.UserCountResponse;
import com.coaxial.dto.UserRequest;
import com.coaxial.dto.UserResponse;
import com.coaxial.entity.User;
import com.coaxial.entity.UserRole;
import com.coaxial.repository.UserRepository;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }



    public List<User> getUsersByRole(UserRole role) {
        System.out.println("UserService: Getting users by role: " + role);
        List<User> users = userRepository.findByRole(role);
        System.out.println("UserService: Found " + users.size() + " users with role " + role);
        for (User user : users) {
            System.out.println("UserService: User - " + user.getUsername() + ", Role: " + user.getRole() + ", Enabled: " + user.getEnabled());
        }
        return users;
    }

    public User createUser(User user) {
        // Encode password if it's not already encoded
        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    public User updateUser(User user) {
        System.out.println("UserService: Updating user with ID: " + user.getId() + ", enabled: " + user.getEnabled());
        User savedUser = userRepository.save(user);
        System.out.println("UserService: User saved successfully with enabled: " + savedUser.getEnabled());
        return savedUser;
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }


    public User createAdminUser() {
        String adminUsername = "admin";
        String adminEmail = "admin@coaxial.com";
        String adminPassword = "admin123";
        
        System.out.println("UserService: Starting admin user creation process...");
        
        // Check if admin user already exists
        if (userRepository.existsByUsername(adminUsername)) {
            System.out.println("UserService: Admin user already exists, returning existing user");
            return userRepository.findByUsername(adminUsername).orElse(null);
        }
        
        System.out.println("UserService: Creating new admin user...");
        
        // Create admin user using UserRequest
        UserRequest adminRequest = new UserRequest();
        adminRequest.setUsername(adminUsername);
        adminRequest.setEmail(adminEmail);
        adminRequest.setPassword(adminPassword);
        adminRequest.setFirstName("Admin");
        adminRequest.setLastName("User");
        adminRequest.setRole(UserRole.ADMIN);
        adminRequest.setEnabled(true);
        adminRequest.setPhoneNumber("1234567890");
        
        try {
            UserResponse userResponse = createUser(adminRequest);
            return userRepository.findById(userResponse.getId()).orElse(null);
        } catch (Exception e) {
            System.err.println("UserService: Error creating admin user: " + e.getMessage());
            throw e;
        }
    }

    // ========== NEW COMPREHENSIVE METHODS ==========

    // Create new user from UserRequest
    public UserResponse createUser(UserRequest userRequest) {
        // Check if username already exists
        if (userRepository.existsByUsername(userRequest.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + userRequest.getUsername());
        }
        
        // Check if email already exists
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + userRequest.getEmail());
        }
        
        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setEmail(userRequest.getEmail());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setRole(userRequest.getRole());
        user.setEnabled(userRequest.getEnabled() != null ? userRequest.getEnabled() : true);
        user.setPhoneNumber(userRequest.getPhoneNumber());
        user.setAddress(userRequest.getAddress());
        user.setBio(userRequest.getBio());
        
        // Parse date of birth if provided
        if (userRequest.getDateOfBirth() != null && !userRequest.getDateOfBirth().isEmpty()) {
            try {
                LocalDate dateOfBirth = LocalDate.parse(userRequest.getDateOfBirth());
                user.setDateOfBirth(dateOfBirth);
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid date format for date of birth");
            }
        }
        
        User savedUser = userRepository.save(user);
        return convertToResponse(savedUser);
    }
    
    // Get all users with pagination and filters
    @Transactional(readOnly = true)
    public Page<UserResponse> getUsers(UserRole role, Boolean enabled, String search, Pageable pageable) {
        List<User> allUsers = getAllUsersList(role, enabled, search);
        
        // Manual pagination since we're using simple JPA methods
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allUsers.size());
        List<User> pageContent = allUsers.subList(start, end);
        
        Page<User> users = new PageImpl<>(pageContent, pageable, allUsers.size());
        return users.map(this::convertToResponse);
    }
    
    // Get all users without pagination (for admin management)
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers(UserRole role, Boolean enabled, String search) {
        List<User> users;
        
        if (role != null && enabled != null && search != null && !search.isEmpty()) {
            // First get users by role and enabled status, then filter by search
            List<User> filteredUsers = userRepository.findByRoleAndEnabled(role, enabled);
            users = filteredUsers.stream()
                    .filter(user -> user.getFirstName().toLowerCase().contains(search.toLowerCase()) ||
                                   user.getLastName().toLowerCase().contains(search.toLowerCase()) ||
                                   user.getUsername().toLowerCase().contains(search.toLowerCase()) ||
                                   user.getEmail().toLowerCase().contains(search.toLowerCase()))
                    .collect(Collectors.toList());
        } else if (role != null && enabled != null) {
            users = userRepository.findByRoleAndEnabled(role, enabled);
        } else if (role != null) {
            users = userRepository.findByRole(role);
        } else if (enabled != null) {
            users = userRepository.findByEnabled(enabled);
        } else if (search != null && !search.isEmpty()) {
            users = userRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                search, search, search, search);
        } else {
            users = userRepository.findAll();
        }
        
        return users.stream().map(this::convertToResponse).collect(Collectors.toList());
    }
    
    // Get user by ID (returns UserResponse)
    @Transactional(readOnly = true)
    public UserResponse getUserResponseById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        return convertToResponse(user);
    }
    
    // Get user by username (returns UserResponse)
    @Transactional(readOnly = true)
    public UserResponse getUserResponseByUsername(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username));
        return convertToResponse(user);
    }
    
    // Update user from UserRequest
    public UserResponse updateUser(Long id, UserRequest userRequest) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        
        // Check if username is being changed and if it already exists
        if (!user.getUsername().equals(userRequest.getUsername()) && 
            userRepository.existsByUsername(userRequest.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + userRequest.getUsername());
        }
        
        // Check if email is being changed and if it already exists
        if (!user.getEmail().equals(userRequest.getEmail()) && 
            userRepository.existsByEmail(userRequest.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + userRequest.getEmail());
        }
        
        // Update fields
        user.setUsername(userRequest.getUsername());
        user.setEmail(userRequest.getEmail());
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setRole(userRequest.getRole());
        user.setEnabled(userRequest.getEnabled());
        user.setPhoneNumber(userRequest.getPhoneNumber());
        user.setAddress(userRequest.getAddress());
        user.setBio(userRequest.getBio());
        
        // Update password only if provided
        if (userRequest.getPassword() != null && !userRequest.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        }
        
        // Parse date of birth if provided
        if (userRequest.getDateOfBirth() != null && !userRequest.getDateOfBirth().isEmpty()) {
            try {
                LocalDate dateOfBirth = LocalDate.parse(userRequest.getDateOfBirth());
                user.setDateOfBirth(dateOfBirth);
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid date format for date of birth");
            }
        }
        
        User updatedUser = userRepository.save(user);
        return convertToResponse(updatedUser);
    }
    
    // Activate user
    public UserResponse activateUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        
        user.setEnabled(true);
        User updatedUser = userRepository.save(user);
        return convertToResponse(updatedUser);
    }
    
    // Deactivate user
    public UserResponse deactivateUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        
        user.setEnabled(false);
        User updatedUser = userRepository.save(user);
        return convertToResponse(updatedUser);
    }
    
    // Update last login time
    public void updateLastLogin(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);
        }
    }
    
    // Get user counts
    @Transactional(readOnly = true)
    public UserCountResponse getUserCounts() {
        long totalUsers = userRepository.count();
        long adminCount = userRepository.countByRole(UserRole.ADMIN);
        long instructorCount = userRepository.countByRole(UserRole.INSTRUCTOR);
        long studentCount = userRepository.countByRole(UserRole.STUDENT);
        long activeUsers = userRepository.countByEnabled(true);
        long inactiveUsers = userRepository.countByEnabled(false);
        
        return new UserCountResponse(totalUsers, adminCount, instructorCount, 
                                   studentCount, activeUsers, inactiveUsers);
    }
    
    // Convert User entity to UserResponse DTO
    private UserResponse convertToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setFullName(user.getFullName());
        response.setRole(user.getRole());
        response.setRoleDisplayName(user.getRole().getDisplayName());
        response.setEnabled(user.getEnabled());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        response.setLastLoginAt(user.getLastLoginAt());
        response.setProfileImageUrl(user.getProfileImageUrl());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setDateOfBirth(user.getDateOfBirth());
        response.setAddress(user.getAddress());
        response.setBio(user.getBio());
        return response;
    }
    
    // Helper method to get users list using simplified JPA methods
    private List<User> getAllUsersList(UserRole role, Boolean enabled, String search) {
        if (role != null && enabled != null && search != null && !search.isEmpty()) {
            // First get users by role and enabled status, then filter by search
            List<User> filteredUsers = userRepository.findByRoleAndEnabled(role, enabled);
            return filteredUsers.stream()
                    .filter(user -> user.getFirstName().toLowerCase().contains(search.toLowerCase()) ||
                                   user.getLastName().toLowerCase().contains(search.toLowerCase()) ||
                                   user.getUsername().toLowerCase().contains(search.toLowerCase()) ||
                                   user.getEmail().toLowerCase().contains(search.toLowerCase()))
                    .collect(Collectors.toList());
        } else if (role != null && enabled != null) {
            return userRepository.findByRoleAndEnabled(role, enabled);
        } else if (role != null) {
            return userRepository.findByRole(role);
        } else if (enabled != null) {
            return userRepository.findByEnabled(enabled);
        } else if (search != null && !search.isEmpty()) {
            return userRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                search, search, search, search);
        } else {
            return userRepository.findAll();
        }
    }
}
