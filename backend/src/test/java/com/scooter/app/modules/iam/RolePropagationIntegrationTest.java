package com.scooter.app.modules.iam;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class RolePropagationIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.flyway.enabled", () -> true);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        registry.add("app.jwt.secret", () -> "12345678901234567890123456789012");
        registry.add("app.jwt.expiration-minutes", () -> 60);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String email;
    private static final String PASSWORD = "secret123";

    @BeforeEach
    void setUp() {
        email = "customer-" + UUID.randomUUID() + "@example.com";
        userRepository.saveAndFlush(User.builder()
                .id(UUID.randomUUID())
                .email(email)
                .fullName("Integration Customer")
                .passwordHash(passwordEncoder.encode(PASSWORD))
                .role(UserRole.CUSTOMER)
                .createdAt(LocalDateTime.now())
                .build());
    }

    @Test
    void login_returns_jwt_with_ROLE_CUSTOMER() throws Exception {
        String token = loginAndExtractToken();
        JsonNode payload = decodeJwtPayload(token);

        assertThat(payload.has("authorities")).isTrue();
        assertThat(objectMapper.convertValue(payload.get("authorities"), List.class))
                .contains("ROLE_CUSTOMER");
    }

    @Test
    void customer_ping_ok_with_customer_token() throws Exception {
        String token = loginAndExtractToken();

        mockMvc.perform(get("/api/customer/ping")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void admin_ping_forbidden_with_customer_token() throws Exception {
        String token = loginAndExtractToken();

        mockMvc.perform(get("/api/admin/ping")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void customer_ping_unauthorized_without_token() throws Exception {
        mockMvc.perform(get("/api/customer/ping"))
                .andExpect(status().isUnauthorized());
    }

    private String loginAndExtractToken() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "%s",
                                  "password": "%s"
                                }
                                """.formatted(email, PASSWORD)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode loginBody = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        return loginBody.get("accessToken").asText();
    }

    private JsonNode decodeJwtPayload(String token) throws Exception {
        String[] parts = token.split("\\.");
        assertThat(parts).hasSize(3);

        byte[] payload = Base64.getUrlDecoder().decode(parts[1]);
        return objectMapper.readTree(new String(payload, StandardCharsets.UTF_8));
    }
}
