package com.example.onboarding_api.service;

import com.example.onboarding_api.dto.PersonalInfoDTO;
import com.example.onboarding_api.entity.Onboarding;
import com.example.onboarding_api.entity.PersonalInfo;
import com.example.onboarding_api.entity.User;
import com.example.onboarding_api.repository.OnboardingRepository;
import com.example.onboarding_api.repository.PersonalInfoRepository;
import com.example.onboarding_api.repository.UserRepository;
import com.example.onboarding_api.util.AESUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PersonalInfoService {

    private final PersonalInfoRepository personalInfoRepository;
    private final UserRepository userRepository;
    private final OnboardingRepository onboardingRepository;
    private final AuditLogService auditLogService;

    public PersonalInfoService(
            PersonalInfoRepository personalInfoRepository,
            UserRepository userRepository,
            OnboardingRepository onboardingRepository, AuditLogService auditLogService
    ) {
        this.personalInfoRepository = personalInfoRepository;
        this.userRepository = userRepository;
        this.onboardingRepository = onboardingRepository;
        this.auditLogService = auditLogService;
    }

    /**
     * Método transaccional para guardar o actualizar la información personal de un usuario.
     * Este método garantiza que las operaciones en la base de datos se realicen de manera consistente.
     *
     * @param userId ID del usuario que está actualizando su información personal.
     * @param dto Objeto DTO que contiene los datos personales del usuario.
     * @param httpRequest Objeto HttpServletRequest que proporciona detalles adicionales de la solicitud, como la dirección IP.
     */
    @Transactional
    public void saveOrUpdate(Long userId, PersonalInfoDTO dto, HttpServletRequest httpRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        PersonalInfo info = personalInfoRepository.findByUserId(userId)
                .orElse(new PersonalInfo());

        info.setUser(user);
        info.setEmail(AESUtil.encrypt(dto.getEmail()));
        info.setBirthday(dto.getBirthday());
        info.setPhone(AESUtil.encrypt(dto.getPhone()));

        personalInfoRepository.save(info);

        // Actualiza el paso completado del onboarding
        onboardingRepository.updateOnboardingStep(userId, 2);

        auditLogService.log(userId, "Actualización de datos personales", httpRequest.getRemoteAddr());
    }
}

