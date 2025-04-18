package com.example.onboarding_api.controller;

import com.example.onboarding_api.dto.LoginRequest;
import com.example.onboarding_api.dto.RegisterRequest;
import com.example.onboarding_api.service.AuthService;
import com.example.onboarding_api.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request,
            BindingResult bindingResult,
            HttpServletRequest httpRequest
    ){
        if (bindingResult.hasErrors()) {
            // Procesar los errores de validación
            Map<String, String> errores = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errores.put(error.getField(), error.getDefaultMessage());
            }

            return ResponseEntity.badRequest().body(errores);
        }
        return ResponseEntity.ok(authService.login(request, httpRequest));
    }

    @PostMapping("/register")
    public  ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequest request,
            BindingResult bindingResult,
            HttpServletRequest httpRequest
    ){
        if (bindingResult.hasErrors()) {
            // Procesar los errores de validación
            Map<String, String> errores = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errores.put(error.getField(), error.getDefaultMessage());
            }

            return ResponseEntity.badRequest().body(errores);
        }
        return ResponseEntity.ok(authService.register(request, httpRequest));
    }
}

