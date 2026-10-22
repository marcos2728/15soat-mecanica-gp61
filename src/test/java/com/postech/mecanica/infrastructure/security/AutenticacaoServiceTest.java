package com.postech.mecanica.infrastructure.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.postech.mecanica.domain.enums.PerfilUsuario;
import com.postech.mecanica.domain.repository.UsuarioRepository;
import com.postech.mecanica.infrastructure.persistence.entity.UsuarioEntity;

@ExtendWith(MockitoExtension.class)
class AutenticacaoServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private AutenticacaoService autenticacaoService;

    @Test
    @DisplayName("Deve carregar usuário por login com sucesso")
    void deveCarregarUsuarioComSucesso() {
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setLogin("admin");
        usuario.setSenha("encoded_pass");
        usuario.setPerfilUsuario(PerfilUsuario.GERENTE);

        when(usuarioRepository.findByLogin("admin")).thenReturn(Optional.of(usuario));

        UserDetails userDetails = autenticacaoService.loadUserByUsername("admin");

        assertNotNull(userDetails);
        assertEquals("admin", userDetails.getUsername());
        assertEquals("encoded_pass", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_GERENTE")));
    }

    @Test
    @DisplayName("Deve lançar UsernameNotFoundException quando usuário não for encontrado")
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        when(usuarioRepository.findByLogin("inexistente")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> autenticacaoService.loadUserByUsername("inexistente"));
    }
}
