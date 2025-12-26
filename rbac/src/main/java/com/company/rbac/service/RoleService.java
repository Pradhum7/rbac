package com.company.rbac.service;

import com.company.rbac.dto.request.RoleAssignRequest;
import com.company.rbac.dto.request.RoleCreateRequest;
import com.company.rbac.dto.response.RoleResponse;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<RoleResponse> getAllRoles() {
        log.debug("Fetching all roles");

        return roleRepository.findAll().stream()
                .map(RoleResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RoleResponse getRoleById(Long id) {
        log.debug("Fetching role by ID: {}", id);

        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));

        return RoleResponse.fromEntity(role);
    }

    @Transactional
    public RoleResponse createRole(RoleCreateRequest request) {
        log.info("Creating new role: {}", request.getName());

        String roleName = request.getName().toUpperCase();

        if (roleRepository.existsByName(roleName)) {
            throw new DuplicateResourceException("Role already exists: " + roleName);
        }

        Role role = Role.builder()
                .name(roleName)
                .description(request.getDescription())
                .build();

        Role savedRole = roleRepository.save(role);
        log.info("Role created successfully: {}", savedRole.getName());

        return RoleResponse.fromEntity(savedRole);
    }

    @Transactional
    public UserResponse assignRoleToUser(RoleAssignRequest request) {
        log.info("Assigning role {} to user {}", request.getRoleId(), request.getUserId());

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + request.getRoleId()));

        if (user.getRoles().contains(role)) {
            throw new BadRequestException("User already has this role");
        }

        user.addRole(role);
        User updatedUser = userRepository.save(user);

        log.info("Role {} assigned to user {} successfully", role.getName(), user.getEmail());

        return UserResponse.fromEntity(updatedUser);
    }

    @Transactional
    public UserResponse revokeRoleFromUser(RoleAssignRequest request) {
        log.info("Revoking role {} from user {}", request.getRoleId(), request.getUserId());

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + request.getRoleId()));

        if (!user.getRoles().contains(role)) {
            throw new BadRequestException("User does not have this role");
        }

        if (user.getRoles().size() == 1) {
            throw new BadRequestException("Cannot remove the last role from user");
        }

        user.removeRole(role);
        User updatedUser = userRepository.save(user);

        log.info("Role {} revoked from user {} successfully", role.getName(), user.getEmail());

        return UserResponse.fromEntity(updatedUser);
    }

    @Transactional
    public void deleteRole(Long id) {
        log.info("Deleting role with ID: {}", id);

        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));

        // Check if role is assigned to any users
        if (!role.getUsers().isEmpty()) {
            throw new BadRequestException("Cannot delete role that is assigned to users");
        }

        roleRepository.delete(role);
        log.info("Role deleted successfully: {}", role.getName());
    }
}