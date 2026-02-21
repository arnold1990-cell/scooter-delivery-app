package com.scooter.app.shared.config;

import com.scooter.app.modules.iam.User;
import com.scooter.app.modules.iam.UserRepository;
import com.scooter.app.modules.iam.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminUserSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${ADMIN_EMAIL:}")
    private String adminEmail;

    @Value("${ADMIN_PASSWORD:}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        if (userRepository.existsByRole(UserRole.ADMIN)) {
            log.info("Admin seeder skipped: at least one admin user already exists");
            return;
        }

        if (adminEmail == null || adminEmail.isBlank() || adminPassword == null || adminPassword.isBlank()) {
            log.warn("Admin seeder skipped: ADMIN_EMAIL and ADMIN_PASSWORD must be set when no admin exists");
            return;
        }

        User admin = User.builder()
                .id(UUID.randomUUID())
                .email(adminEmail.trim())
                .passwordHash(passwordEncoder.encode(adminPassword))
                .fullName("System Admin")
                .role(UserRole.ADMIN)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(admin);
        log.info("Admin seeder created bootstrap admin user: {}", admin.getEmail());
    }
}
