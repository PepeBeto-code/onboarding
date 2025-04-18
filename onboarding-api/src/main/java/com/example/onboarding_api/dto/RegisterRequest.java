package com.example.onboarding_api.dto;

import com.example.onboarding_api.validations.UniqueUsername;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @UniqueUsername
    @NotBlank(message = "El nombre de usuario es obligatorio.")
    String username;
    @NotBlank(message = "La contraseña es obligatoria.")
    String password;
}

