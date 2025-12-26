package com.company.rbac.service;

import com.company.rbac.dto.request.UserCreateRequest;
import com.company.rbac.dto.request.UserUpdateRequest;
import com.company.rbac.dto.response.PageResponse;
import com.company.rbac.dto.response.UserResponse;
import com.company.rbac.entity.Role;
import com.company.rbac.entity.User;
import com.company.rbac.exception.BadRequestException;
import com.company.rbac.exception.DuplicateResourceException;
import com.company.rbac.exception.ResourceNotFoundException;
import com.company.rbac.repository.RoleRepository;
import com.company.rbac.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public PageResponse<UserResponse> getAllUsers(Pageable pageable) {
        log.debug("Fetching all users with pagination");

        Page<User> userPage = userRepository.findAll(pageable);
        Page<UserResponse> responsePage = userPage.map(UserResponse::fromEntity);

        return PageResponse.fromPage(responsePage);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        log.debug("Fetching user by ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        return UserResponse.fromEntity(user);
    }

    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        log.info("Creating new user: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists: " + request.getEmail());
        }

        if (request.getRoleIds() == null || request.getRoleIds().isEmpty()) {
            throw new BadRequestException("At least one role must be assigned");
        }

        // Fetch roles
        Set<Role> roles = new HashSet<>();
        for (Long roleId : request.getRoleIds()) {
            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));
            roles.add(role);
        }

        // Create user
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .enabled(true)
                .build();

        // Assign roles
        roles.forEach(user::addRole);

        User savedUser = userRepository.save(user);
        log.info("User created successfully: {}", savedUser.getEmail());

        return UserResponse.fromEntity(savedUser);
    }

    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        log.info("Updating user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Update fields if provided
        if (request.getFirstName() != null && !request.getFirstName().isBlank()) {
            user.setFirstName(request.getFirstName());
        }

        if (request.getLastName() != null && !request.getLastName().isBlank()) {
            user.setLastName(request.getLastName());
        }

        if (request.getEnabled() != null) {
            user.setEnabled(request.getEnabled());
        }

        User updatedUser = userRepository.save(user);
        log.info("User updated successfully: {}", updatedUser.getEmail());

        return UserResponse.fromEntity(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        userRepository.delete(user);
        log.info("User deleted successfully: {}", user.getEmail());
    }

    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        log.debug("Fetching user by email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        return UserResponse.fromEntity(user);
    }
}