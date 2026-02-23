package com.scooter.app.shared.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RoleProbeController {

    @GetMapping("/customers/ping")
    public String customerPing() {
        return "ok";
    }
}
