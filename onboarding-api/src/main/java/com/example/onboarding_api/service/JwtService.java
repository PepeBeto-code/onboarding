package com.example.onboarding_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

/**
 * Servicio encargado de manejar la generación, validación y extracción de información de JWT (JSON Web Tokens).
 */
@Service
@RequiredArgsConstructor
public class JwtService {

    // Inyección de UserDetailsService para obtener detalles de los usuarios del sistema.
    private final UserDetailsService userDetailsService;

    // Clave secreta utilizada para firmar y verificar los tokens. Es importante mantenerla segura.
    private String secretKey = "naBAHSDHSDHhbbdg73trgedggdgedegjhjagssywg2wh2jshhsjwhs";

    /**
     * Genera un token JWT para un usuario dado.
     * @param user Detalles del usuario que se asociará al token.
     * @return Token JWT firmado y válido.
     */
    public String getToken(UserDetails user) {
        return getToken(new HashMap<>(), user);
    }

    /**
     * Genera un token JWT con claims personalizados y asociado a un usuario específico.
     * @param kvHashMap Claims adicionales que se pueden incluir en el token.
     * @param user Detalles del usuario que se asociará al token.
     * @return Token JWT firmado.
     */
    private String getToken(Map<String, Object> kvHashMap, UserDetails user) {
        return Jwts.builder()
                .setClaims(kvHashMap) // Añadir claims personalizados al token.
                .setSubject(user.getUsername())  // Establecer el username como subject del token.
                .setIssuedAt(new Date())         // Fecha en que el token es emitido.
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 7)) // Expira en 7 días.
                .signWith(getKey(), SignatureAlgorithm.HS256) // Firmar el token con clave secreta y algoritmo HS256.
                .compact();
    }

    /**
     * Convierte la clave secreta en una instancia de Key utilizando el algoritmo HMAC SHA.
     * @return Instancia de Key basada en la clave secreta.
     */
    private Key getKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Extrae el username (subject) desde un token JWT.
     * @param token El token JWT del que se extraerá el username.
     * @return Username contenido en el token.
     */
    public String getUsernameFromToken(String token) {
        try {
            return getClaim(token, Claims::getSubject);
        } catch (ExpiredJwtException e) {
            // Permite extraer el username incluso si el token está expirado.
            return e.getClaims().getSubject();
        } catch (JwtException e) {
            throw new IllegalArgumentException("Error al procesar el token: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene todos los claims de un token JWT.
     * @param token El token JWT del que se extraerán los claims.
     * @return Claims completos del token.
     */
    private Claims getAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey()) // Establece la clave de firma para validar el token.
                .build()
                .parseClaimsJws(token) // Parsear el token y obtener los claims.
                .getBody();
    }

    /**
     * Obtiene un claim específico de un token JWT utilizando una función de resolución de claims.
     * @param token El token JWT del que se extraerá el claim.
     * @param claimsResolver Función que define qué claim se desea extraer.
     * @return El valor del claim especificado.
     */
    public <T> T getClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Obtiene la fecha de expiración de un token JWT.
     * @param token El token JWT del que se extraerá la fecha de expiración.
     * @return Fecha de expiración del token.
     */
    private Date getExpiration(String token) {
        return getClaim(token, Claims::getExpiration);
    }

    /**
     * Verifica si un token JWT ha expirado.
     * @param token El token JWT que se verificará.
     * @return True si el token ha expirado, false en caso contrario.
     */
    private boolean isTokenExpired(String token) {
        return getExpiration(token).before(new Date());
    }

    /**
     * Verifica la validez de un token JWT con respecto a un usuario específico.
     *
     * @param token El token JWT que se quiere validar.
     * @param userDetails Los detalles del usuario asociado al token, obtenidos desde el servicio de autenticación.
     * @return true si el token es válido (el username coincide y no está expirado), false en caso contrario.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username=getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername())&& !isTokenExpired(token));
    }
}
