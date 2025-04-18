package com.example.onboarding_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressInfoDTO {
    private String street;
    private String number;
    private String postalCode;
    private String city;
}
