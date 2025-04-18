package com.example.onboarding_api.util;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * AESUtil es una clase utilitaria para realizar operaciones de encriptación y desencriptación
 * utilizando el algoritmo AES en modo CBC con PKCS5Padding.
 * Este enfoque asegura que los datos estén protegidos mediante cifrado simétrico.
 */
@Component
public class AESUtil {
    // Clave secreta utilizada para encriptar y desencriptar datos.
    @Value("${aes.secret.key}")
    private String secretKeyConfig;
    // Vector de inicialización (IV) utilizado para el modo CBC.
    @Value("${aes.init.vector}")
    private  String initVectorConfig;

    // Variables estáticas que necesitan ser utilizadas en un contexto estático.
    private static String SECRET_KEY;
    private static String INIT_VECTOR;

    /**
     * Inicializa las variables estáticas utilizando los valores inyectados por Spring.
     * Este método se ejecuta automáticamente después de que el bean ha sido creado.
     */
    @PostConstruct
    public void init() {
        SECRET_KEY = this.secretKeyConfig;
        INIT_VECTOR = this.initVectorConfig;
    }

    /**
     * Encripta una cadena de texto utilizando AES en modo CBC con PKCS5Padding.
     *
     * @param value Cadena de texto que será encriptada.
     * @return Cadena encriptada en formato Base64.
     * @throws RuntimeException si ocurre algún error durante el proceso de encriptación.
     */
    public static String encrypt(String value) {
        try {
            IvParameterSpec iv = new IvParameterSpec(INIT_VECTOR.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(SECRET_KEY.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding"); // Modo CBC
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());
            return Base64.getEncoder().encodeToString(encrypted); // Codificamos a texto
        } catch (Exception ex) {
            throw new RuntimeException("Error al encriptar: " + ex.getMessage(), ex);
        }
    }

    /**
     * Desencripta una cadena en formato Base64 utilizando AES en modo CBC con PKCS5Padding.
     *
     * @param encrypted Cadena en formato Base64 que será desencriptada.
     * @return Cadena de texto original después de desencriptarla.
     * @throws RuntimeException si ocurre algún error durante el proceso de desencriptación.
     */
    public static String decrypt(String encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec(INIT_VECTOR.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(SECRET_KEY.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(Base64.getDecoder().decode(encrypted));

            return new String(original);
        } catch (Exception ex) {
            throw new RuntimeException("Error al desencriptar: " + ex.getMessage(), ex);
        }
    }
}
