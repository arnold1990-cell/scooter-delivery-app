package com.scooter.app.modules.admin;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminPingController {

    @GetMapping("/ping")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String ping() {
        return "ok";
    }
}
