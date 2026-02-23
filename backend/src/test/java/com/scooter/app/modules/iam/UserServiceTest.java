package com.scooter.app.modules.iam;

import com.scooter.app.modules.iam.dto.AuthResponse;
import com.scooter.app.modules.iam.dto.RegisterRequest;
import com.scooter.app.modules.riders.RiderRepository;
import com.scooter.app.shared.jwt.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RiderRepository riderRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserDetailsServiceImpl userDetailsService;
    @Mock
    private JwtService jwtService;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, riderRepository, passwordEncoder, authenticationManager, userDetailsService, jwtService);
    }

    @Test
    void registerAssignsCustomerRoleWhenRoleMissing() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("new-customer@example.com");
        request.setPassword("password123");
        request.setFullName("New Customer");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.getPassword())).thenReturn("hashed-password");
        when(userRepository.saveAndFlush(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(request.getEmail())
                .password("hashed-password")
                .authorities("ROLE_CUSTOMER")
                .build();
        when(userDetailsService.loadUserByUsername(request.getEmail())).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("jwt-token");

        AuthResponse response = userService.register(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).saveAndFlush(userCaptor.capture());
        assertThat(userCaptor.getValue().getRole()).isEqualTo(UserRole.CUSTOMER);

        assertThat(response.getRoles()).containsExactly("CUSTOMER");
        assertThat(response.getAccessToken()).isEqualTo("jwt-token");
    }
}
