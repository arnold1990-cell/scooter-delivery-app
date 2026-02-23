package com.scooter.app.shared.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityRoleAccessTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void customerRoleCanReachCustomerPath() throws Exception {
        mockMvc.perform(get("/api/customer/non-existent"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "RIDER")
    void riderRoleCannotReachCustomerPath() throws Exception {
        mockMvc.perform(get("/api/customer/non-existent"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "RIDER")
    void riderRoleCanReachRiderPath() throws Exception {
        mockMvc.perform(get("/api/rider/non-existent"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminRoleCanReachAdminPath() throws Exception {
        mockMvc.perform(get("/api/admin/non-existent"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void customerRoleCannotReachAdminPath() throws Exception {
        mockMvc.perform(get("/api/admin/non-existent"))
                .andExpect(status().isForbidden());
    }
}
