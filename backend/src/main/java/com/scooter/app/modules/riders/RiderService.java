package com.scooter.app.modules.riders;

import com.scooter.app.modules.iam.User;
import com.scooter.app.modules.iam.UserRepository;
import com.scooter.app.modules.riders.dto.RiderLocationResponse;
import com.scooter.app.modules.riders.dto.RiderLocationUpdateRequest;
import com.scooter.app.modules.riders.dto.RiderProfileResponse;
import com.scooter.app.shared.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RiderService {

    private final RiderRepository riderRepository;
    private final RiderLocationRepository riderLocationRepository;
    private final UserRepository userRepository;

    public RiderProfileResponse getMe(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        RiderProfile profile = riderRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Rider profile not found"));
        return toResponse(profile, user);
    }

    @Transactional
    public RiderProfileResponse setOnline(String email, boolean online) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        RiderProfile profile = riderRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Rider profile not found"));
        profile.setIsOnline(online);
        if (!online) {
            profile.setStatus(RiderStatus.OFFLINE);
        }
        riderRepository.save(profile);
        return toResponse(profile, user);
    }

    @Transactional
    public RiderProfileResponse setStatus(String email, RiderStatus status) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found"));
        RiderProfile profile = riderRepository.findByUserId(user.getId()).orElseThrow(() -> new EntityNotFoundException("Rider profile not found"));
        profile.setStatus(status);
        profile.setIsOnline(status != RiderStatus.OFFLINE);
        riderRepository.save(profile);
        return toResponse(profile, user);
    }

    @Transactional
    public RiderLocationResponse updateMyLocation(String email, RiderLocationUpdateRequest request) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found"));
        riderRepository.findByUserId(user.getId()).orElseThrow(() -> new EntityNotFoundException("Rider profile not found"));
        RiderLocation location = riderLocationRepository.findById(user.getId())
                .orElse(RiderLocation.builder().riderId(user.getId()).build());
        location.setLatitude(request.getLatitude());
        location.setLongitude(request.getLongitude());
        location.setUpdatedAt(LocalDateTime.now());
        return toLocationResponse(riderLocationRepository.save(location));
    }

    public RiderLocationResponse getLocation(UUID riderId) {
        RiderLocation location = riderLocationRepository.findById(riderId)
                .orElseThrow(() -> new EntityNotFoundException("Rider location not found"));
        return toLocationResponse(location);
    }

    @Transactional
    public RiderProfileResponse approve(UUID userId, ApprovalStatus status) {
        RiderProfile profile = riderRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Rider profile not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        profile.setApprovalStatus(status);
        riderRepository.save(profile);
        return toResponse(profile, user);
    }

    public List<RiderProfileResponse> all() {
        return riderRepository.findAll().stream().map(profile -> {
            User user = userRepository.findById(profile.getUserId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));
            return toResponse(profile, user);
        }).toList();
    }

    private RiderProfileResponse toResponse(RiderProfile profile, User user) {
        return RiderProfileResponse.builder()
                .id(profile.getId())
                .userId(profile.getUserId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .licenseNumber(profile.getLicenseNumber())
                .approvalStatus(profile.getApprovalStatus())
                .isOnline(profile.getIsOnline())
                .status(profile.getStatus())
                .build();
    }

    private RiderLocationResponse toLocationResponse(RiderLocation location) {
        return RiderLocationResponse.builder()
                .riderId(location.getRiderId())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .updatedAt(location.getUpdatedAt())
                .build();
    }
}
