package com.example.onboarding_api.service;

import com.example.onboarding_api.dto.AuthResponse;
import com.example.onboarding_api.dto.LoginRequest;
import com.example.onboarding_api.dto.RegisterRequest;
import com.example.onboarding_api.dto.UserDto;
import com.example.onboarding_api.entity.User;
import com.example.onboarding_api.enums.Role;
import com.example.onboarding_api.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    public AuthResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();

        UserDto userOut = UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .build();

        auditLogService.log(
                user.getId(),
                "Inicio de sesión",
                httpRequest.getRemoteAddr());

        String token=jwtService.getToken(user);
        return AuthResponse.builder()
                .token(token)
                .completeOnboarding(user.getOnboarding() != null && user.getOnboarding().isComplete())
                .initOnboarding(user.getOnboarding() != null)
                .user(userOut)
                .build();
    }

    public AuthResponse register(RegisterRequest request,  HttpServletRequest httpRequest) {
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode( request.getPassword()))
                .role(Role.USER)
                .build();
        userRepository.save(user);

        auditLogService.log(
                user.getId(),
                "Registro de usuario",
                httpRequest.getRemoteAddr());

        return AuthResponse.builder()
                .token(jwtService.getToken(user)).build();
    }
}

