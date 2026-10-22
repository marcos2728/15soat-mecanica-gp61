package com.postech.mecanica.infrastructure.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", "mySecretKeyForJwtTokenGenerationMustBeLongEnough123456!");
        ReflectionTestUtils.setField(jwtService, "expiration", 3600000L); // 1 hora

        userDetails = new User("admin", "password", List.of(new SimpleGrantedAuthority("ROLE_GERENTE")));
    }

    @Test
    @DisplayName("Deve gerar token, extrair login e validar token com sucesso")
    void deveGerarEValidarToken() {
        String token = jwtService.gerarToken(userDetails);

        assertNotNull(token);
        assertEquals("admin", jwtService.extrairLogin(token));
        assertTrue(jwtService.tokenValido(token, userDetails));
    }

    @Test
    @DisplayName("Deve invalidar token quando o usuário for diferente")
    void deveInvalidarTokenParaUsuarioDiferente() {
        String token = jwtService.gerarToken(userDetails);
        UserDetails outroUsuario = new User("outro", "password", List.of(new SimpleGrantedAuthority("ROLE_MECANICO")));

        assertFalse(jwtService.tokenValido(token, outroUsuario));
    }

    @Test
    @DisplayName("Deve invalidar token expirado")
    void deveInvalidarTokenExpirado() {
        ReflectionTestUtils.setField(jwtService, "expiration", -1000L); // expirado no passado
        String token = jwtService.gerarToken(userDetails);

        assertThrowsJwtExceptionOrFalse(() -> jwtService.tokenValido(token, userDetails));
    }

    private void assertThrowsJwtExceptionOrFalse(Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            assertTrue(e instanceof io.jsonwebtoken.ExpiredJwtException);
        }
    }
}
