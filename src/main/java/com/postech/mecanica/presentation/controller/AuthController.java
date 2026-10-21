package com.postech.mecanica.presentation.controller;

import com.postech.mecanica.application.dto.LoginRequestDTO;
import com.postech.mecanica.application.dto.LoginResponseDTO;
import com.postech.mecanica.infrastructure.security.JwtService;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getLogin(), request.getSenha()));

        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        String token = jwtService.gerarToken(userDetails);

        return new LoginResponseDTO(token);
    }
}