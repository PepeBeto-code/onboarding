package com.example.onboarding_api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonalInfoDTO {
    @NotNull(message = "El email no puede ser nula")
    private String email;
    @NotNull(message = "El cumpleaños no puede ser nulo")
    private LocalDate birthday;
    @NotNull(message = "El telefono no puede ser nula")
    private String phone;
}

