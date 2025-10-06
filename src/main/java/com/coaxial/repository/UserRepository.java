package com.coaxial.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coaxial.entity.User;
import com.coaxial.entity.UserRole;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find by username and email
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    
    // Check existence
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    // Find by role and status
    List<User> findByRole(UserRole role);
    List<User> findByEnabled(Boolean enabled);
    List<User> findByRoleAndEnabled(UserRole role, Boolean enabled);

    // Search by name, username, or email
    List<User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(
        String firstName, String lastName, String username, String email);

    // Count methods
    long countByRole(UserRole role);
    long countByEnabled(Boolean enabled);
    long countByRoleAndEnabled(UserRole role, Boolean enabled);

    // Find by date
    List<User> findByCreatedAtAfter(LocalDateTime date);
    List<User> findByLastLoginAtAfter(LocalDateTime date);
    List<User> findByLastLoginAtIsNullOrLastLoginAtBefore(LocalDateTime date);
}