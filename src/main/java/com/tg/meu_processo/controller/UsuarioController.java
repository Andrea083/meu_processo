package com.tg.meu_processo.controller;

import com.tg.meu_processo.dto.*;
import com.tg.meu_processo.service.UsuarioService;
import lombok.RequiredArgsConstructor;
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

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public List<UsuarioDTO> listar() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or @authenticatedUserService.isOwner(#id)")
    public UsuarioDTO buscar(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Map<String, Object>> criar(@RequestBody UsuarioCreateDTO dto) {
        return ResponseEntity.ok(service.criarPorAdmin(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or @authenticatedUserService.isOwner(#id)")
    public UsuarioDTO atualizar(@PathVariable Long id, @RequestBody UsuarioCreateDTO dto) {
        return service.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/senha")
    @PreAuthorize("@authenticatedUserService.isOwner(#id)")
    public ResponseEntity<Map<String, String>> alterarSenha(
            @PathVariable Long id,
            @RequestBody SenhaUpdateDTO dto) {
        service.alterarSenha(id, dto);
        return ResponseEntity.ok(Map.of("mensagem", "Senha alterada com sucesso"));
    }

    @PostMapping("/recuperar-senha")
    public ResponseEntity<Map<String, String>> recuperarSenha(@RequestBody RecuperarSenhaDTO dto) {
        return ResponseEntity.ok(service.recuperarSenha(dto.email()));
    }
}
