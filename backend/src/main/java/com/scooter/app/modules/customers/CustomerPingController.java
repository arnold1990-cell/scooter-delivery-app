package com.scooter.app.modules.customers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customer")
public class CustomerPingController {

    @GetMapping("/ping")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public String ping() {
        return "ok";
    }
}
