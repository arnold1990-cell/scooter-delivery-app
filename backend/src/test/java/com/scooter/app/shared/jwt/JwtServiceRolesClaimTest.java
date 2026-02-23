package com.scooter.app.shared.jwt;

import com.scooter.app.modules.iam.User;
import com.scooter.app.modules.iam.UserRepository;
import com.scooter.app.modules.iam.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceRolesClaimTest {

    @Mock
    private UserRepository userRepository;

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(userRepository, new JwtRoleMapper());
        ReflectionTestUtils.setField(jwtService, "secret", "12345678901234567890123456789012");
        ReflectionTestUtils.setField(jwtService, "expirationMinutes", 60L);
    }

    @Test
    void generateTokenStoresAuthoritiesClaimWithRolePrefix() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .email("customer@example.com")
                .passwordHash("hash")
                .fullName("Customer")
                .role(UserRole.CUSTOMER)
                .createdAt(LocalDateTime.now())
                .build();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        UserDetails details = org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
                .password("hash")
                .authorities("ROLE_CUSTOMER")
                .build();

        String token = jwtService.generateToken(details);

        List<String> extractedAuthorities = jwtService.extractAuthorities(token);
        assertThat(extractedAuthorities).containsExactly("ROLE_CUSTOMER");
    }
}
