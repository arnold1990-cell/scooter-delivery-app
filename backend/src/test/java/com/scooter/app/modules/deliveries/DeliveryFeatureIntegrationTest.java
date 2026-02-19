package com.scooter.app.modules.deliveries;

import com.scooter.app.modules.deliveries.dto.CreateDeliveryRequest;
import com.scooter.app.modules.deliveries.dto.DeliveryResponse;
import com.scooter.app.modules.deliveries.dto.UpdateStatusRequest;
import com.scooter.app.modules.iam.User;
import com.scooter.app.modules.iam.UserRepository;
import com.scooter.app.modules.iam.UserRole;
import com.scooter.app.modules.riders.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class DeliveryFeatureIntegrationTest {

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
    private DeliveryService deliveryService;
    @Autowired
    private DeliveryStatusHistoryRepository historyRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RiderRepository riderRepository;
    @Autowired
    private RiderLocationRepository riderLocationRepository;

    @Test
    void statusUpdateCreatesHistory() {
        User customer = userRepository.saveAndFlush(User.builder().id(UUID.randomUUID()).email("flow-customer@example.com")
                .passwordHash("x").fullName("Flow Customer").role(UserRole.CUSTOMER).createdAt(LocalDateTime.now()).build());

        CreateDeliveryRequest request = new CreateDeliveryRequest();
        request.setPickupAddress("A");
        request.setDropoffAddress("B");
        request.setPickupLatitude(new BigDecimal("12.9716"));
        request.setPickupLongitude(new BigDecimal("77.5946"));
        request.setDropoffLatitude(new BigDecimal("12.9352"));
        request.setDropoffLongitude(new BigDecimal("77.6245"));

        DeliveryResponse delivery = deliveryService.create(customer.getEmail(), request);

        UpdateStatusRequest statusRequest = new UpdateStatusRequest();
        statusRequest.setStatus(DeliveryStatus.CANCELLED);
        deliveryService.updateStatus(customer.getEmail(), ChangedByRole.ADMIN, delivery.getId(), statusRequest);

        assertThat(historyRepository.findByDeliveryIdOrderByCreatedAtAsc(delivery.getId())).hasSize(2);
    }

    @Test
    void assignNearestAssignsAvailableRider() {
        User customer = userRepository.saveAndFlush(User.builder().id(UUID.randomUUID()).email("assign-customer@example.com")
                .passwordHash("x").fullName("Assign Customer").role(UserRole.CUSTOMER).createdAt(LocalDateTime.now()).build());
        User rider = userRepository.saveAndFlush(User.builder().id(UUID.randomUUID()).email("rider@example.com")
                .passwordHash("x").fullName("Rider One").role(UserRole.RIDER).createdAt(LocalDateTime.now()).build());

        riderRepository.saveAndFlush(RiderProfile.builder().id(UUID.randomUUID()).userId(rider.getId()).approvalStatus(ApprovalStatus.APPROVED)
                .isOnline(true).status(RiderStatus.AVAILABLE).createdAt(LocalDateTime.now()).build());
        riderLocationRepository.saveAndFlush(RiderLocation.builder().riderId(rider.getId()).latitude(new BigDecimal("12.9716"))
                .longitude(new BigDecimal("77.5946")).updatedAt(LocalDateTime.now()).build());

        CreateDeliveryRequest request = new CreateDeliveryRequest();
        request.setPickupAddress("A");
        request.setDropoffAddress("B");
        request.setPickupLatitude(new BigDecimal("12.9716"));
        request.setPickupLongitude(new BigDecimal("77.5946"));
        request.setDropoffLatitude(new BigDecimal("12.9352"));
        request.setDropoffLongitude(new BigDecimal("77.6245"));

        DeliveryResponse delivery = deliveryService.create(customer.getEmail(), request);
        DeliveryResponse assigned = deliveryService.assignNearest(delivery.getId());

        assertThat(assigned.getRiderId()).isEqualTo(rider.getId());
        assertThat(assigned.getStatus()).isEqualTo(DeliveryStatus.ASSIGNED);
    }
}
