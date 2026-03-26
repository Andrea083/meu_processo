package com.tg.meu_processo.controller;

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
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String senha = request.get("senha");

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElse(null);

        if (usuario == null || !passwordEncoder.matches(senha, usuario.getSenha())) {
            return ResponseEntity.status(401).body(Map.of("error", "Credenciais inválidas"));
        }

        String token = jwtService.generateToken(email, usuario.getPerfil().name(), usuario.getId());

        return ResponseEntity.ok(Map.of(
                "token", token,
                "perfil", usuario.getPerfil(),
                "nome", usuario.getNome(),
                "userId", usuario.getId()
        ));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email já cadastrado"));
        }

        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        Usuario saved = usuarioRepository.save(usuario);

        return ResponseEntity.ok(Map.of("message", "Usuário criado", "id", saved.getId()));
    }
}

