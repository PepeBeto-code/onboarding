package com.example.onboarding_api.service;

import com.example.onboarding_api.entity.AuditLog;
import com.example.onboarding_api.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * AuditLogService es una clase de servicio encargada de registrar y almacenar
 * logs de auditoría en la base de datos. Estos logs contienen información detallada
 * de las acciones realizadas por los usuarios, junto con sus direcciones IP.
 */
@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    /**
     * Método para registrar un log de auditoría en la base de datos.
     * Este log también se imprime en la consola para fines de monitoreo.
     *
     * @param userId ID del usuario que realizó la acción.
     * @param action Descripción de la acción realizada (ej. "Actualización de dirección").
     * @param ip Dirección IP del cliente que realizó la acción.
     */
    public void log(Long userId, String action, String ip) {
        // Generar el log para archivo
        String formattedLog = String.format("[%s] Usuario ID: %d - Acción: %s - IP: %s",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                userId, action, ip
        );

        AuditLog log = AuditLog.builder()
                .userId(userId)
                .action(action)
                .ip(ip)
                .description(formattedLog)
                .timestamp(LocalDateTime.now())
                .build();

        System.out.println(formattedLog);

        auditLogRepository.save(log);
    }
}

