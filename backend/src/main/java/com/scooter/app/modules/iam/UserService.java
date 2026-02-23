package com.scooter.app.modules.iam;

import com.scooter.app.modules.iam.dto.AuthResponse;
import com.scooter.app.modules.iam.dto.LoginRequest;
import com.scooter.app.modules.iam.dto.RegisterRequest;
import com.scooter.app.modules.riders.ApprovalStatus;
import com.scooter.app.modules.riders.RiderProfile;
import com.scooter.app.modules.riders.RiderRepository;
import com.scooter.app.modules.riders.RiderStatus;
import com.scooter.app.shared.exception.EntityNotFoundException;
import com.scooter.app.shared.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final RiderRepository riderRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        UserRole role = request.getRole() == null || request.getRole().isBlank()
                ? UserRole.CUSTOMER
                : UserRole.from(request.getRole());
        if (role == UserRole.ADMIN) {
            throw new IllegalArgumentException("ADMIN cannot self-register");
        }
        userRepository.findByEmail(request.getEmail()).ifPresent(u -> {
            throw new IllegalArgumentException("Email already registered");
        });

        User user = User.builder()
                .id(UUID.randomUUID())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(role)
                .createdAt(LocalDateTime.now())
                .build();
        try {
            userRepository.saveAndFlush(user);
        } catch (DataIntegrityViolationException ex) {
            log.error("Registration failed for email={} due to data integrity violation", request.getEmail(), ex);
            throw new IllegalArgumentException("Email already registered");
        }

        if (role == UserRole.RIDER) {
            riderRepository.save(RiderProfile.builder()
                    .id(UUID.randomUUID())
                    .userId(user.getId())
                    .approvalStatus(ApprovalStatus.PENDING)
                    .isOnline(false)
                    .status(RiderStatus.OFFLINE)
                    .createdAt(LocalDateTime.now())
                    .build());
        }

        UserDetails details = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtService.generateToken(details);
        return toAuthResponse(user, token);
    }

    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (BadCredentialsException ex) {
            log.warn("Login failed for email={} due to bad credentials", request.getEmail());
            throw ex;
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        UserDetails details = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtService.generateToken(details);
        return toAuthResponse(user, token);
    }

    public User me(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    private AuthResponse toAuthResponse(User user, String accessToken) {
        String roleName = user.getRole().name();
        return AuthResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(List.of(roleName))
                .accessToken(accessToken)
                .token(accessToken)
                .build();
    }
}
