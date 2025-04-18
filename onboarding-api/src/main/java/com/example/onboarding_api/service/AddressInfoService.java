package com.example.onboarding_api.service;

import com.example.onboarding_api.dto.AddressInfoDTO;
import com.example.onboarding_api.entity.AddressInfo;
import com.example.onboarding_api.entity.Onboarding;
import com.example.onboarding_api.entity.User;
import com.example.onboarding_api.repository.AddressInfoRepository;
import com.example.onboarding_api.repository.OnboardingRepository;
import com.example.onboarding_api.repository.UserRepository;
import com.example.onboarding_api.util.AESUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AddressInfoService {

    private final AddressInfoRepository addressInfoRepository;
    private final UserRepository userRepository;
    private final OnboardingRepository onboardingRepository;
    private final AuditLogService auditLogService;

    public AddressInfoService(
            AddressInfoRepository addressInfoRepository,
            UserRepository userRepository,
            OnboardingRepository onboardingRepository, AuditLogService auditLogService
    ) {
        this.addressInfoRepository = addressInfoRepository;
        this.userRepository = userRepository;
        this.onboardingRepository = onboardingRepository;
        this.auditLogService = auditLogService;
    }

    /**
     * Guarda o actualiza la información de dirección de un usuario y registra avances en el onboarding.
     * Este método está marcado como transaccional para garantizar la consistencia en las operaciones de base de datos.
     *
     * @param userId ID del usuario que está actualizando su información de dirección.
     * @param dto Objeto DTO que contiene los datos de la dirección que serán procesados.
     * @param httpRequest Objeto HttpServletRequest que proporciona información adicional de la solicitud, como la dirección IP.
     */
    @Transactional
    public void saveOrUpdate(Long userId, AddressInfoDTO dto, HttpServletRequest httpRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        AddressInfo address = addressInfoRepository.findByUserId(userId)
                .orElse(new AddressInfo());

        address.setUser(user);
        address.setStreet(AESUtil.encrypt(dto.getStreet()));
        address.setNumber(AESUtil.encrypt(dto.getNumber()));
        address.setPostalCode(AESUtil.encrypt(dto.getPostalCode()));
        address.setCity(AESUtil.encrypt(dto.getCity()));

        addressInfoRepository.save(address);

        // Avanzar paso del onboarding
        onboardingRepository.updateOnboardingStep(userId, 3);

        auditLogService.log(userId, "Actualización de direccion", httpRequest.getRemoteAddr());
    }
}

