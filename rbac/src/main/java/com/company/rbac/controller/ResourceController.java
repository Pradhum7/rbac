package com.company.rbac.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
@Slf4j
public class ResourceController {

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getUserDashboard(Authentication authentication) {
        log.info("GET /api/resources/dashboard - Access by: {}", authentication.getName());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Welcome to your dashboard");
        response.put("user", authentication.getName());
        response.put("roles", authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        response.put("data", Map.of(
                "notifications", 5,
                "tasks", 12,
                "messages", 3
        ));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/reports")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getManagerReports(Authentication authentication) {
        log.info("GET /api/resources/reports - Access by: {}", authentication.getName());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Manager reports data");
        response.put("user", authentication.getName());
        response.put("reports", java.util.List.of(
                Map.of(
                        "id", 1,
                        "title", "Q1 Performance Report",
                        "createdBy", "manager@example.com",
                        "date", "2024-03-31"
                ),
                Map.of(
                        "id", 2,
                        "title", "Team Productivity Analysis",
                        "createdBy", "manager@example.com",
                        "date", "2024-03-28"
                ),
                Map.of(
                        "id", 3,
                        "title", "Budget Overview",
                        "createdBy", "admin@example.com",
                        "date", "2024-03-25"
                )
        ));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin-panel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAdminPanel(Authentication authentication) {
        log.info("GET /api/resources/admin-panel - Access by: {}", authentication.getName());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Admin panel data");
        response.put("user", authentication.getName());
        response.put("systemStats", Map.of(
                "totalUsers", 150,
                "activeUsers", 120,
                "inactiveUsers", 30,
                "totalRoles", 4,
                "systemUptime", "99.9%",
                "lastBackup", "2024-03-31 23:00:00"
        ));
        response.put("recentActivity", java.util.List.of(
                Map.of("action", "User created", "by", "admin@example.com", "timestamp", "2024-03-31 14:30:00"),
                Map.of("action", "Role assigned", "by", "admin@example.com", "timestamp", "2024-03-31 14:25:00"),
                Map.of("action", "User deleted", "by", "admin@example.com", "timestamp", "2024-03-31 14:20:00")
        ));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/public")
    public ResponseEntity<Map<String, Object>> getPublicResource() {
        log.info("GET /api/resources/public - Public access");

        Map<String, Object> response = new HashMap<>();
        response.put("message", "This is a public resource");
        response.put("data", "Available to everyone");

        return ResponseEntity.ok(response);
    }
}