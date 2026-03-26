package com.tg.meu_processo.controller;

import com.tg.meu_processo.dto.ProcessoCreateDTO;
import com.tg.meu_processo.dto.ProcessoDTO;
import com.tg.meu_processo.service.ProcessoService;
import com.tg.meu_processo.security.AuthenticatedUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/processos")
@RequiredArgsConstructor
public class ProcessoController {

    private final ProcessoService service;
    private final AuthenticatedUserService authenticatedUserService;

    @GetMapping
    public List<ProcessoDTO> listar() {
        if (authenticatedUserService.isAdmin()) {
            return service.listarTodos();
        } else if (authenticatedUserService.isAdvogado()) {
            return service.listarPorAdvogado(authenticatedUserService.getUsuarioId());
        } else if (authenticatedUserService.isCliente()) {
            return service.listarPorCliente(authenticatedUserService.getUsuarioId());
        }
        return List.of();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProcessoDTO> buscar(@PathVariable Long id) {
        ProcessoDTO processo = service.buscarPorId(id);
        if (podeAcessar(processo)) {
            return ResponseEntity.ok(processo);
        }
        return ResponseEntity.status(403).build();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADVOGADO')")
    public ProcessoDTO criar(@RequestBody ProcessoCreateDTO dto) {
        return service.criar(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADVOGADO')")
    public ProcessoDTO atualizar(@PathVariable Long id, @RequestBody ProcessoCreateDTO dto) {
        return service.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    private boolean podeAcessar(ProcessoDTO p) {
        if (authenticatedUserService.isAdmin()) return true;
        if (authenticatedUserService.isAdvogado()) return p.advogadoId().equals(authenticatedUserService.getUsuarioId());
        if (authenticatedUserService.isCliente()) return p.clienteId().equals(authenticatedUserService.getUsuarioId());
        return false;
    }
}
