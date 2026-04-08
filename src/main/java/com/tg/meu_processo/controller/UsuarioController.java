package com.tg.meu_processo.controller;

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
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService service;
    private final AuthenticatedUserService authService;

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
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Map<String, Object>> criar(@Valid @RequestBody UsuarioCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criarPorAdmin(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> atualizar(@PathVariable Long id, @RequestBody UsuarioCreateDTO dto) {
        if (authService.isAdmin() || authService.isOwner(id)) {
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

    @PostMapping("/recuperar-senha")
    public ResponseEntity<Map<String, String>> recuperarSenha(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(service.recuperarSenha(body.get("email")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletar(@PathVariable Long id) {
        if (authService.isAdmin() || authService.isOwner(id)) {
            if (!service.existePorId(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
            }
            service.deletar(id);
            return ResponseEntity.ok("Usuário deletado com sucesso");
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado");
    }
}
