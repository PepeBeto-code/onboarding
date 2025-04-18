package com.example.onboarding_api.service;

import com.example.onboarding_api.dto.OnboardingStatusDTO;
import com.example.onboarding_api.entity.Onboarding;
import com.example.onboarding_api.entity.User;
import com.example.onboarding_api.repository.OnboardingRepository;
import com.example.onboarding_api.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class OnboardingService {

    private final OnboardingRepository onboardingRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    public OnboardingStatusDTO getByUserId(Long userId, HttpServletRequest httpRequest) {
        Onboarding onboarding = onboardingRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
                    Onboarding newOnboarding = new Onboarding();
                    newOnboarding.setUser(user);
                    return onboardingRepository.save(newOnboarding);
                });

        auditLogService.log(
                userId,
                "Inicia Onboarding",
                httpRequest.getRemoteAddr());

        return OnboardingStatusDTO.builder()
                .isComplete(onboarding.isComplete())
                .stepCompleted(onboarding.getStepCompleted())
                .build();
    }
}

