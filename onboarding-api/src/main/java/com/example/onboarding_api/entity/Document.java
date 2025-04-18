package com.example.onboarding_api.entity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@Data
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne // Un usuario puede subir varios documentos
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String docType;

    private String fileName;

    private String fileType;

    @Column(columnDefinition = "TEXT")
    private String fileBase64;

    private LocalDateTime uploadedAt = LocalDateTime.now();

}

