package com.scooter.app.modules.riders;

import com.scooter.app.modules.iam.User;
import com.scooter.app.modules.iam.UserRepository;
import com.scooter.app.modules.riders.dto.RiderProfileResponse;
import com.scooter.app.shared.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RiderService {

    private final RiderRepository riderRepository;
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
        riderRepository.save(profile);
        return toResponse(profile, user);
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
                .build();
    }
}
