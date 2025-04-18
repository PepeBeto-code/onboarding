package com.example.onboarding_api.config;

import com.example.onboarding_api.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.http.HttpHeaders;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String token = getTokenFromRequest(request);

        final String username;

        if (token==null)
        {
            filterChain.doFilter(request, response);
            return;
        }

        username=jwtService.getUsernameFromToken(token);

        if (username!=null )
        {
            UserDetails userDetails=userDetailsService.loadUserByUsername(username);

            if (jwtService.isTokenValid(token, userDetails))
            {
                UsernamePasswordAuthenticationToken authToken= new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);

                // Verificar si ya hay un usuario en el contexto de seguridad
                Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();
                if (existingAuth != null && existingAuth.isAuthenticated()) {
                    System.out.println("🔹 Usuario ya autenticado en el contexto y filtro: " + existingAuth.getName());
                }else {
                    System.out.println("No hay usuario autenticado y filtro");
                }

            }

        }

        filterChain.doFilter(request,response);

    }

    private String getTokenFromRequest(HttpServletRequest request) {

        // Buscar en los encabezados HTTP
        final String autHeader = request.getHeader("Authorization");
        if (StringUtils.hasText(autHeader) && autHeader.startsWith("Bearer")){
            return autHeader.substring(7);
        }

        return null;
    }
}
