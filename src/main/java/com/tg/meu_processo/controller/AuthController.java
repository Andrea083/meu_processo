package com.tg.meu_processo.controller;

import com.tg.meu_processo.dto.LoginDTO;
import com.tg.meu_processo.entity.Usuario;
import com.tg.meu_processo.repository.UsuarioRepository;
import com.tg.meu_processo.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO dto) {
        try {
            Usuario usuario = usuarioRepository.findByEmail(dto.email())
                    .orElse(null);

            if (usuario == null || !passwordEncoder.matches(dto.senha(), usuario.getSenha())) {
                return ResponseEntity.status(401).body(Map.of("erro", "Credenciais inválidas"));
            }

            String token = jwtService.generateToken(usuario.getEmail(), usuario.getPerfil().name(), usuario.getId());

            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "perfil", usuario.getPerfil(),
                    "nome", usuario.getNome(),
                    "id", usuario.getId()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("erro", e.getMessage()));
        }
    }
}

