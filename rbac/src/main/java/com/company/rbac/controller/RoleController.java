package com.company.rbac.controller;

import com.company.rbac.dto.request.RoleAssignRequest;
import com.company.rbac.dto.request.RoleCreateRequest;
import com.company.rbac.dto.response.ApiResponse;
import com.company.rbac.dto.response.RoleResponse;
import com.company.rbac.dto.response.UserResponse;
import com.company.rbac.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<List<RoleResponse>> getAllRoles() {
        log.info("GET /api/roles - Get all roles");

        List<RoleResponse> response = roleService.getAllRoles();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleResponse> getRoleById(@PathVariable Long id) {
        log.info("GET /api/roles/{} - Get role by ID", id);

        RoleResponse response = roleService.getRoleById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<RoleResponse> createRole(@Valid @RequestBody RoleCreateRequest request) {
        log.info("POST /api/roles - Create new role: {}", request.getName());

        RoleResponse response = roleService.createRole(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/assign")
    public ResponseEntity<ApiResponse<UserResponse>> assignRoleToUser(
            @Valid @RequestBody RoleAssignRequest request
    ) {
        log.info("POST /api/roles/assign - Assign role {} to user {}",
                request.getRoleId(), request.getUserId());

        UserResponse user = roleService.assignRoleToUser(request);
        return ResponseEntity.ok(ApiResponse.success("Role assigned successfully", user));
    }

    @DeleteMapping("/revoke")
    public ResponseEntity<ApiResponse<UserResponse>> revokeRoleFromUser(
            @Valid @RequestBody RoleAssignRequest request
    ) {
        log.info("DELETE /api/roles/revoke - Revoke role {} from user {}",
                request.getRoleId(), request.getUserId());

        UserResponse user = roleService.revokeRoleFromUser(request);
        return ResponseEntity.ok(ApiResponse.success("Role revoked successfully", user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRole(@PathVariable Long id) {
        log.info("DELETE /api/roles/{} - Delete role", id);

        roleService.deleteRole(id);
        return ResponseEntity.ok(ApiResponse.success("Role deleted successfully"));
    }
}