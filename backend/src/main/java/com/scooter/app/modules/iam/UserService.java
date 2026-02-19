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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RiderRepository riderRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        UserRole role = UserRole.from(request.getRole());
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
        userRepository.save(user);

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
        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        UserDetails details = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtService.generateToken(details);
        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .build();
    }

    public User me(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }
}
