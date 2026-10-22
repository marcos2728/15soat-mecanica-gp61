package com.postech.mecanica.infrastructure.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import jakarta.servlet.ServletException;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private JwtAuthFilter jwtAuthFilter;

    @BeforeEach
    @AfterEach
    void cleanUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Deve passar direto pelo filtro quando header Authorization for ausente ou sem Bearer")
    void devePassarDiretoSemHeader() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        jwtAuthFilter.doFilterInternal(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Deve autenticar com sucesso quando token for válido")
    void deveAutenticarComTokenValido() throws ServletException, IOException {
        String token = "valid_jwt_token";
        String login = "joao";

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        UserDetails userDetails = new User(login, "pass", List.of(new SimpleGrantedAuthority("ROLE_GERENTE")));

        when(jwtService.extrairLogin(token)).thenReturn(login);
        when(userDetailsService.loadUserByUsername(login)).thenReturn(userDetails);
        when(jwtService.tokenValido(token, userDetails)).thenReturn(true);

        jwtAuthFilter.doFilterInternal(request, response, chain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(login, SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Test
    @DisplayName("Não deve autenticar quando token for inválido")
    void naoDeveAutenticarComTokenInvalido() throws ServletException, IOException {
        String token = "invalid_jwt_token";
        String login = "joao";

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        UserDetails userDetails = new User(login, "pass", List.of(new SimpleGrantedAuthority("ROLE_GERENTE")));

        when(jwtService.extrairLogin(token)).thenReturn(login);
        when(userDetailsService.loadUserByUsername(login)).thenReturn(userDetails);
        when(jwtService.tokenValido(token, userDetails)).thenReturn(false);

        jwtAuthFilter.doFilterInternal(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
