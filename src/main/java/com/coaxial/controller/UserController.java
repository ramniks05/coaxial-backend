package com.coaxial.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.coaxial.dto.UserCountResponse;
import com.coaxial.dto.UserRequest;
import com.coaxial.dto.UserResponse;
import com.coaxial.entity.User;
import com.coaxial.entity.UserRole;
import com.coaxial.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "User Management", description = "User management APIs for admins")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users", description = "Admin can view all users with optional filtering and search")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<User>> getAllUsers(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean enabled) {
        
        System.out.println("AdminController: getAllUsers called with role=" + role + ", search=" + search + ", enabled=" + enabled);
        
        List<User> users;
        
        // Filter by role if specified
        if (role != null && !role.isEmpty()) {
            try {
                UserRole userRole = UserRole.valueOf(role.toUpperCase());
                System.out.println("AdminController: Filtering by role: " + userRole);
                users = userService.getUsersByRole(userRole);
                System.out.println("AdminController: Found " + users.size() + " users with role " + userRole);
            } catch (IllegalArgumentException e) {
                System.out.println("AdminController: Invalid role provided: " + role + ", returning all users");
                // Invalid role, return all users
                users = userService.getAllUsers();
            }
        } else {
            System.out.println("AdminController: No role filter, getting all users");
            users = userService.getAllUsers();
        }
        
        // Filter by enabled status if specified
        if (enabled != null) {
            System.out.println("AdminController: Filtering by enabled status: " + enabled);
            int beforeCount = users.size();
            users = users.stream()
                    .filter(user -> enabled.equals(user.getEnabled()))
                    .toList();
            System.out.println("AdminController: After enabled filter: " + users.size() + " users (was " + beforeCount + ")");
        }
        
        // Search functionality
        if (search != null && !search.isEmpty()) {
            System.out.println("AdminController: Filtering by search: " + search);
            int beforeCount = users.size();
            users = users.stream()
                    .filter(user -> 
                        (user.getFirstName() != null && user.getFirstName().toLowerCase().contains(search.toLowerCase())) ||
                        (user.getLastName() != null && user.getLastName().toLowerCase().contains(search.toLowerCase())) ||
                        (user.getEmail() != null && user.getEmail().toLowerCase().contains(search.toLowerCase())) ||
                        (user.getUsername() != null && user.getUsername().toLowerCase().contains(search.toLowerCase()))
                    )
                    .toList();
            System.out.println("AdminController: After search filter: " + users.size() + " users (was " + beforeCount + ")");
        }
        
        System.out.println("AdminController: Returning " + users.size() + " users");
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/count")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get user count by role", description = "Get count of users by role")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User count retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Object> getUserCountByRole() {
        List<User> allUsers = userService.getAllUsers();
        
        long adminCount = allUsers.stream().filter(u -> u.getRole() == UserRole.ADMIN).count();
        long instructorCount = allUsers.stream().filter(u -> u.getRole() == UserRole.INSTRUCTOR).count();
        long studentCount = allUsers.stream().filter(u -> u.getRole() == UserRole.STUDENT).count();
        
        long activeCount = allUsers.stream().filter(u -> Boolean.TRUE.equals(u.getEnabled())).count();
        long inactiveCount = allUsers.stream().filter(u -> Boolean.FALSE.equals(u.getEnabled())).count();
        
        return ResponseEntity.ok(java.util.Map.of(
            "total", allUsers.size(),
            "admins", adminCount,
            "instructors", instructorCount,
            "students", studentCount,
            "active", activeCount,
            "inactive", inactiveCount
        ));
    }

    @GetMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get user by ID", description = "Admin can view a specific user by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Object> getUserById(@PathVariable Long userId) {
        try {
            User user = userService.getUserById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
            
            return ResponseEntity.ok(java.util.Map.of(
                "success", true,
                "user", java.util.Map.of(
                    "id", user.getId(),
                    "username", user.getUsername(),
                    "email", user.getEmail(),
                    "firstName", user.getFirstName(),
                    "lastName", user.getLastName(),
                    "role", user.getRole(),
                    "enabled", user.getEnabled(),
                    "createdAt", user.getCreatedAt(),
                    "updatedAt", user.getUpdatedAt()
                )
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @PutMapping("/users/{userId}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activate user", description = "Admin can activate a user account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User activated successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Object> activateUser(@PathVariable Long userId) {
        try {
            User user = userService.getUserById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
            
            user.setEnabled(true);
            userService.updateUser(user);
            
            return ResponseEntity.ok(java.util.Map.of(
                "success", true,
                "message", "User activated successfully",
                "userId", userId,
                "username", user.getUsername()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @PutMapping("/users/{userId}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deactivate user", description = "Admin can deactivate a user account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User deactivated successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Object> deactivateUser(@PathVariable Long userId) {
        try {
            System.out.println("AdminController: Attempting to deactivate user with ID: " + userId);
            
            User user = userService.getUserById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
            
            System.out.println("AdminController: Found user: " + user.getUsername() + " with role: " + user.getRole());
            
            // Prevent admin from deactivating themselves
            if (user.getRole() == UserRole.ADMIN) {
                System.out.println("AdminController: Cannot deactivate admin user");
                return ResponseEntity.badRequest().body(java.util.Map.of(
                    "success", false,
                    "message", "Cannot deactivate admin user"
                ));
            }
            
            System.out.println("AdminController: Current enabled status: " + user.getEnabled());
            user.setEnabled(false);
            System.out.println("AdminController: Setting enabled to false");
            
            User updatedUser = userService.updateUser(user);
            System.out.println("AdminController: User updated successfully, new enabled status: " + updatedUser.getEnabled());
            
            return ResponseEntity.ok(java.util.Map.of(
                "success", true,
                "message", "User deactivated successfully",
                "userId", userId,
                "username", user.getUsername(),
                "enabled", updatedUser.getEnabled()
            ));
        } catch (Exception e) {
            System.err.println("AdminController: Error deactivating user: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", "Failed to deactivate user: " + e.getMessage()
            ));
        }
    }

    @DeleteMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete user", description = "Admin can delete a user account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User deleted successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Object> deleteUser(@PathVariable Long userId) {
        try {
            User user = userService.getUserById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
            
            // Prevent admin from deleting themselves
            if (user.getRole() == UserRole.ADMIN) {
                return ResponseEntity.badRequest().body(java.util.Map.of(
                    "success", false,
                    "message", "Cannot delete admin user"
                ));
            }
            
            String username = user.getUsername();
            userService.deleteUser(userId);
            
            return ResponseEntity.ok(java.util.Map.of(
                "success", true,
                "message", "User deleted successfully",
                "userId", userId,
                "username", username
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    // ========== NEW COMPREHENSIVE ENDPOINTS ==========

    // Get all users with filters and pagination (new comprehensive version)
    @GetMapping("/users/v2")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users with pagination", description = "Get users with advanced filtering, search, and pagination")
    public ResponseEntity<Page<UserResponse>> getUsers(
            @RequestParam(required = false) UserRole role,
            @RequestParam(required = false) Boolean enabled,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<UserResponse> users = userService.getUsers(role, enabled, search, pageable);
        return ResponseEntity.ok(users);
    }
    
    // Get all users without pagination (new comprehensive version)
    @GetMapping("/users/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users without pagination", description = "Get all users with filtering and search")
    public ResponseEntity<List<UserResponse>> getAllUsersV2(
            @RequestParam(required = false) UserRole role,
            @RequestParam(required = false) Boolean enabled,
            @RequestParam(required = false) String search) {
        
        List<UserResponse> users = userService.getAllUsers(role, enabled, search);
        return ResponseEntity.ok(users);
    }
    
    // Get user by ID (new comprehensive version)
    @GetMapping("/users/{id}/v2")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get user by ID", description = "Get user details by ID with comprehensive information")
    public ResponseEntity<UserResponse> getUserByIdV2(@PathVariable Long id) {
        UserResponse user = userService.getUserResponseById(id);
        return ResponseEntity.ok(user);
    }
    
    // Get user by username (new comprehensive version)
    @GetMapping("/users/username/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get user by username", description = "Get user details by username")
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable String username) {
        UserResponse user = userService.getUserResponseByUsername(username);
        return ResponseEntity.ok(user);
    }
    
    // Create new user (new comprehensive version)
    @PostMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create new user", description = "Create a new user with comprehensive information")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest userRequest) {
        UserResponse user = userService.createUser(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
    
    // Update user (new comprehensive version)
    @PutMapping("/users/{id}/v2")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update user", description = "Update user with comprehensive information")
    public ResponseEntity<UserResponse> updateUserV2(@PathVariable Long id, 
                                                  @Valid @RequestBody UserRequest userRequest) {
        UserResponse user = userService.updateUser(id, userRequest);
        return ResponseEntity.ok(user);
    }
    
    // Get user counts (new comprehensive version)
    @GetMapping("/users/count/v2")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get user statistics", description = "Get comprehensive user statistics")
    public ResponseEntity<UserCountResponse> getUserCountsV2() {
        UserCountResponse counts = userService.getUserCounts();
        return ResponseEntity.ok(counts);
    }
}