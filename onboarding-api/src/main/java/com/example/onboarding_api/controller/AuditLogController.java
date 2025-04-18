package com.example.onboarding_api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.example.onboarding_api.entity.AuditLog;
import com.example.onboarding_api.repository.AuditLogRepository;

/**
 * AuditLogController es una clase que expone un endpoint REST para gestionar los
 * logs de auditoría almacenados en la base de datos. Permite obtener todos los registros
 * mediante una operación GET.
 */
@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class AuditLogController {
    private final AuditLogRepository auditLogRepository;

    /**
     * Endpoint para obtener todos los registros de auditoría.
     * Este método realiza una consulta directa al repositorio para recuperar todos los logs.
     *
     * @return Lista de objetos AuditLog que representan los registros almacenados en la base de datos.
     */
    @GetMapping
    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findAll();
    }
}

