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

//@RestController + @RequestMapping("/api/auth"):
// expõe endpoints REST sob o caminho /api/auth.

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
            //Busca o usuário pelo e-mail.
            Usuario usuario = usuarioRepository.findByEmail(dto.email())
                    .orElse(null);
            //se o usuário não existe ou a senha não confere (passwordEncoder.matches compara a senha digitada
            // com o hash salvo) → retorna 401 .
            if (usuario == null || !passwordEncoder.matches(dto.senha(), usuario.getSenha())) {
                return ResponseEntity.status(401).body(Map.of("erro", "Credenciais inválidas"));
            }
            //se válido, gera um token JWT com e-mail, perfil e id do usuário.
            String token = jwtService.generateToken(usuario.getEmail(), usuario.getPerfil().name(), usuario.getId());
            //Retorna 200 com o token + dados básicos (perfil, nome, id) para o frontend usar.
            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "perfil", usuario.getPerfil(),
                    "nome", usuario.getNome(),
                    "id", usuario.getId()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("erro", e.getMessage()));
        }   //Erro inesperado → retorna 500 com a mensagem
    }
}

/* controlador de autenticação
 É a porta de entrada do sistema.
 O token gerado aqui é o que autoriza o usuário a acessar seus perfis individuais
 e os dados jurídicos (originais e traduzidos) nas demais rotas protegidas.
*/

