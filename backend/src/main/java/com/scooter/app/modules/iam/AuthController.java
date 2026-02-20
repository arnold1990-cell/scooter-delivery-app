package com.scooter.app.modules.iam;

import com.scooter.app.modules.iam.dto.AuthResponse;
import com.scooter.app.modules.iam.dto.LoginRequest;
import com.scooter.app.modules.iam.dto.MeResponse;
import com.scooter.app.modules.iam.dto.RegisterRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return userService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return userService.login(request);
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public MeResponse me(Authentication authentication) {
        User user = userService.me(authentication.getName());
        return MeResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .roles(List.of(user.getRole().name()))
                .build();
    }
}
