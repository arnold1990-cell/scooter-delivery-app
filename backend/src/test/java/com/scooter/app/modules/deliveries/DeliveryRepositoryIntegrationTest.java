package com.scooter.app.modules.deliveries;

import com.scooter.app.modules.iam.User;
import com.scooter.app.modules.iam.UserRepository;
import com.scooter.app.modules.iam.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DeliveryRepositoryIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.flyway.enabled", () -> true);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
    }

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void savesDeliveryWithPostgresEnumStatus() {
        User customer = userRepository.saveAndFlush(User.builder()
                .id(UUID.randomUUID())
                .email("delivery-customer@example.com")
                .passwordHash("hashed-password")
                .fullName("Delivery Customer")
                .role(UserRole.CUSTOMER)
                .createdAt(LocalDateTime.now())
                .build());

        Delivery delivery = Delivery.builder()
                .id(UUID.randomUUID())
                .customerId(customer.getId())
                .pickupAddress("A street")
                .dropoffAddress("B avenue")
                .price(new BigDecimal("14.50"))
                .status(DeliveryStatus.REQUESTED)
                .notes("Fragile")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Delivery savedDelivery = deliveryRepository.saveAndFlush(delivery);

        assertThat(savedDelivery.getId()).isEqualTo(delivery.getId());
        assertThat(savedDelivery.getStatus()).isEqualTo(DeliveryStatus.REQUESTED);
    }
}
