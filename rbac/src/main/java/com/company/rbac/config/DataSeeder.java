package com.company.rbac.config;

import com.company.rbac.entity.Role;
import com.company.rbac.entity.User;
import com.company.rbac.repository.RoleRepository;
import com.company.rbac.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        seedRoles();
        seedDefaultAdmin();
    }

    private void seedRoles() {
        List<String> roleNames = Arrays.asList("ADMIN", "MANAGER", "USER");

        for (String roleName : roleNames) {
            if (!roleRepository.existsByName(roleName)) {
                Role role = Role.builder()
                        .name(roleName)
                        .description(getDefaultDescription(roleName))
                        .build();
                roleRepository.save(role);
                log.info("Created default role: {}", roleName);
            }
        }
    }

    private void seedDefaultAdmin() {
        String adminEmail = "admin@example.com";

        if (!userRepository.existsByEmail(adminEmail)) {
            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseThrow(() -> new RuntimeException("ADMIN role not found"));

            User admin = User.builder()
                    .email(adminEmail)
                    .password(passwordEncoder.encode("Admin@123"))
                    .firstName("System")
                    .lastName("Administrator")
                    .enabled(true)
                    .build();

            admin.addRole(adminRole);
            userRepository.save(admin);

            log.info("Created default admin user: {}", adminEmail);
            log.info("Default admin password: Admin@123");
            log.warn("IMPORTANT: Change the default admin password in production!");
        }
    }

    private String getDefaultDescription(String roleName) {
        return switch (roleName) {
            case "ADMIN" -> "Administrator with full system access";
            case "MANAGER" -> "Manager with elevated permissions";
            case "USER" -> "Standard user with basic access";
            default -> "Custom role";
        };
    }
}