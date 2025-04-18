package com.example.onboarding_api.controller;

import com.example.onboarding_api.dto.AddressInfoDTO;
import com.example.onboarding_api.dto.DocumentDTO;
import com.example.onboarding_api.dto.OnboardingStatusDTO;
import com.example.onboarding_api.dto.PersonalInfoDTO;
import com.example.onboarding_api.entity.AddressInfo;
import com.example.onboarding_api.entity.Document;
import com.example.onboarding_api.entity.Onboarding;
import com.example.onboarding_api.entity.PersonalInfo;
import com.example.onboarding_api.repository.AddressInfoRepository;
import com.example.onboarding_api.repository.DocumentRepository;
import com.example.onboarding_api.repository.PersonalInfoRepository;
import com.example.onboarding_api.repository.UserRepository;
import com.example.onboarding_api.service.AddressInfoService;
import com.example.onboarding_api.service.DocumentService;
import com.example.onboarding_api.service.OnboardingService;
import com.example.onboarding_api.service.PersonalInfoService;
import com.example.onboarding_api.util.AESUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/onboarding")
@AllArgsConstructor
public class OnboardingController {

    private final PersonalInfoService personalInfoService;
    private final PersonalInfoRepository personalInfoRepository;
    private final AddressInfoService addressInfoService;
    private final AddressInfoRepository addressInfoRepository;
    private final UserRepository userRepository;
    private final OnboardingService onboardingService;
    private final DocumentService documentService;
    private final DocumentRepository documentRepository;

    /**
     * Endpoint para obtener el estado del proceso de onboarding del usuario.
     * @param httpRequest HttpServletRequest para obtener detalles de la solicitud HTTP.
     * @return ResponseEntity con el estado del onboarding.
     */
    @GetMapping
    public ResponseEntity<?> getOnboardingUser(HttpServletRequest httpRequest) {
        Long userId = getUserIdFromPrincipal();
        OnboardingStatusDTO onboarding = onboardingService.getByUserId(userId, httpRequest);
        return ResponseEntity.ok(onboarding);
    }

    // --- Paso 1: Información Personal ---

    /**
     * Endpoint para guardar o actualizar información personal del usuario.
     * @param personalInfoDTO Objeto que contiene los datos personales del usuario.
     * @param httpRequest HttpServletRequest para registrar información adicional de la solicitud.
     * @return ResponseEntity indicando éxito.
     */
    @PostMapping("/step/personal")
    public ResponseEntity<?> savePersonalInfo(
            @RequestBody PersonalInfoDTO personalInfoDTO,
            HttpServletRequest httpRequest
    ) {
        Long userId = getUserIdFromPrincipal();
        personalInfoService.saveOrUpdate(userId, personalInfoDTO, httpRequest);
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint para obtener la información personal del usuario.
     * @return ResponseEntity con los datos personales, o código 204 si no hay datos.
     */
    @GetMapping("/step/personal")
    public ResponseEntity<?> getPersonalInfo() {
        Long userId = getUserIdFromPrincipal();
        Optional<PersonalInfo> personal = personalInfoRepository.findByUserId(userId);

        if (personal.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 si no hay datos
        }

        PersonalInfo info = personal.get();
        PersonalInfoDTO dto = new PersonalInfoDTO();
        dto.setEmail(AESUtil.decrypt(info.getEmail()));
        dto.setPhone(AESUtil.decrypt(info.getPhone()));
        dto.setBirthday(info.getBirthday());

        return ResponseEntity.ok(dto);
    }


    // --- Paso 2: Dirección ---

    /**
     * Endpoint para guardar o actualizar la dirección del usuario.
     * @param addressInfoDTO Objeto que contiene los datos de dirección del usuario.
     * @param httpRequest HttpServletRequest para registrar información adicional de la solicitud.
     * @return ResponseEntity indicando éxito.
     */
    @PostMapping("/step/address")
    public ResponseEntity<?> saveAddressInfo(
            @RequestBody AddressInfoDTO addressInfoDTO,
            HttpServletRequest httpRequest
    ) {
        Long userId = getUserIdFromPrincipal();
        addressInfoService.saveOrUpdate(userId, addressInfoDTO, httpRequest);
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint para obtener la dirección del usuario.
     * @return ResponseEntity con los datos de dirección, o código 204 si no hay datos.
     */
    @GetMapping("/step/address")
    public ResponseEntity<?> getAddressInfo() {
        Long userId = getUserIdFromPrincipal();
        Optional<AddressInfo> address = addressInfoRepository.findByUserId(userId);

        if (address.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content si no hay nada
        }

        AddressInfo ai = address.get();
        AddressInfoDTO dto = new AddressInfoDTO();
        dto.setStreet(AESUtil.decrypt(ai.getStreet()));
        dto.setNumber(AESUtil.decrypt(ai.getNumber()));
        dto.setPostalCode(AESUtil.decrypt(ai.getPostalCode()));
        dto.setCity(AESUtil.decrypt(ai.getCity()));

        return ResponseEntity.ok(dto);
    }

    // --- Paso 3: Documentos ---

    /**
     * Endpoint para subir y guardar un documento del usuario.
     * @param dto Objeto que contiene los datos del documento, incluyendo el archivo en Base64.
     * @param httpRequest HttpServletRequest para registrar información adicional de la solicitud.
     * @return ResponseEntity indicando éxito.
     */
    @PostMapping(value = "/step/documents")
    public ResponseEntity<?> uploadDocument(
            @RequestBody DocumentDTO dto,
            HttpServletRequest httpRequest
    ) {
        Long userId = getUserIdFromPrincipal();
        documentService.saveDocument(
                userId,
                dto.getDocType(),
                dto.getFileType(),
                dto.getFileName(),
                dto.getFileBase64(),
                httpRequest
        );
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint para obtener los documentos del usuario.
     * @return ResponseEntity con los datos del documento, o código 204 si no hay datos.
     */
    @GetMapping("/step/documents")
    public ResponseEntity<DocumentDTO> getDocuments() {
        Long userId = getUserIdFromPrincipal();
        Optional<Document> document = documentRepository.findByUserId(userId);

        if (document.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content si no hay nada
        }

        Document doc = document.get();
        DocumentDTO dto = new DocumentDTO(
                AESUtil.decrypt(doc.getDocType()),
                AESUtil.decrypt(doc.getFileType()),
                AESUtil.decrypt(doc.getFileName()),
                AESUtil.decrypt(doc.getFileBase64()));

        return ResponseEntity.ok(dto);
    }

    /**
     * Método auxiliar para obtener el ID del usuario autenticado desde el contexto de seguridad.
     * @return ID del usuario autenticado.
     */
    private Long getUserIdFromPrincipal() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"))
                .getId();
    }
}

