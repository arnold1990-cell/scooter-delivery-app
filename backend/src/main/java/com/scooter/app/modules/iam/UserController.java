package com.scooter.app.modules.iam;

import com.scooter.app.modules.iam.dto.MeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

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
