package com.example.onboarding_api.service;

import com.example.onboarding_api.entity.Document;
import com.example.onboarding_api.entity.Onboarding;
import com.example.onboarding_api.entity.PersonalInfo;
import com.example.onboarding_api.entity.User;
import com.example.onboarding_api.repository.DocumentRepository;
import com.example.onboarding_api.repository.OnboardingRepository;
import com.example.onboarding_api.repository.UserRepository;
import com.example.onboarding_api.util.AESUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final OnboardingRepository onboardingRepository;
    private final AuditLogService auditLogService;

    /**
     * Método transaccional para guardar o actualizar un documento de un usuario.
     * Este método está diseñado para garantizar que las operaciones de base de datos sean consistentes.
     *
     * @param userId ID del usuario que está subiendo el documento.
     * @param docType Tipo de documento (por ejemplo, identificación, comprobante).
     * @param fileType Tipo de archivo (por ejemplo, PDF, PNG).
     * @param fileName Nombre del archivo cargado.
     * @param base64WithPrefix Cadena en Base64 que contiene el archivo, posiblemente con prefijo.
     * @param httpRequest HttpServletRequest para registrar detalles adicionales de la solicitud, como la dirección IP.
     */
    @Transactional
    public void saveDocument(
            Long userId,
            String docType,
            String fileType,
            String fileName,
            String base64WithPrefix,
            HttpServletRequest httpRequest
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Si el base64 viene con el prefijo, lo quitamos
        String base64Clean = base64WithPrefix.contains(",")
                ? base64WithPrefix.split(",")[1]
                : base64WithPrefix;

        Document doc = documentRepository.findByUserId(userId)
                .orElse(new Document());

        doc.setUser(user);
        doc.setFileType(AESUtil.encrypt(fileType));
        doc.setFileName(AESUtil.encrypt(fileName));
        doc.setDocType(AESUtil.encrypt(docType));
        doc.setFileBase64(AESUtil.encrypt(base64Clean));
        documentRepository.save(doc);

        // Paso 3 completado
        onboardingRepository.updateOnboardingStep(userId, 4);

        auditLogService.log(userId, "Documento cargado", httpRequest.getRemoteAddr());
    }
}

