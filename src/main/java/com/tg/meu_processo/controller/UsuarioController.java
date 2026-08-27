package com.tg.meu_processo.controller;

import com.tg.meu_processo.dto.RecuperarSenhaDTO;
import com.tg.meu_processo.dto.SenhaUpdateDTO;
import com.tg.meu_processo.dto.UsuarioCreateDTO;
import com.tg.meu_processo.dto.UsuarioDTO;
import com.tg.meu_processo.security.AuthenticatedUserService;
import com.tg.meu_processo.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000") //libera chamadas do front rodando nessa porta (React/Next)
@RequestMapping("/api/usuarios") // rota base dos métodos
@RequiredArgsConstructor // Lombok gera o construtor com os campos final
public class UsuarioController {

    private final UsuarioService service; // lógica de negócio de usuários
    private final AuthenticatedUserService authService; // identifica quem está logado

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<UsuarioDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> buscarPorId(@PathVariable Long id) {
        if (authService.isAdmin() || authService.isOwner(id)) {
            return ResponseEntity.ok(service.buscarPorId(id));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')") // só admin pode criar
    public ResponseEntity<Map<String, Object>> criar(@Valid @RequestBody UsuarioCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criarPorAdmin(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> atualizar(@PathVariable Long id, @RequestBody UsuarioCreateDTO dto) {
        if (authService.isAdmin() || authService.isOwner(id)) { //condição de admin OU dono
            return ResponseEntity.ok(service.atualizar(id, dto));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PutMapping("/{id}/senha")
    public ResponseEntity<String> alterarSenha(@PathVariable Long id, @RequestBody SenhaUpdateDTO dto) {
        if (authService.isOwner(id)) {
            service.alterarSenha(id, dto);
            return ResponseEntity.ok("Senha alterada com sucesso");
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado");
    }

    @PostMapping("/recuperar-senha") //com e-mail validado
    public ResponseEntity<Map<String, String>> recuperarSenha(@Valid @RequestBody RecuperarSenhaDTO dto) {
        return ResponseEntity.ok(service.recuperarSenha(dto.email()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletar(@PathVariable Long id) {
        if (authService.isAdmin() || authService.isOwner(id)) { //condição de admin OU dono
            if (!service.existePorId(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
            }
            service.deletar(id);
            return ResponseEntity.ok("Usuário deletado com sucesso");
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado");
    }
}

// @CrossOrigin — sem ele, o navegador bloquearia (por CORS) as requisições vindas do front em localhost:3000.
// @Valid: aciona a validação dos campos do DTO (ex.: @NotBlank, @Email). Se algo for inválido, o Spring rejeita antes de executar o método.
// Sem restrição de acesso — porque quem esqueceu a senha ainda não está logado. Recebe um JSON simples tipo { "email": "x@y.com" } e lê o campo com body.get("email").
// Padrão geral: operações amplas (listar/criar) são exclusivas do admin; operações sobre um usuário específico liberam o próprio dono; senha é intransferível.