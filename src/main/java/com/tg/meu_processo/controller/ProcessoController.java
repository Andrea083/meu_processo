package com.tg.meu_processo.controller;

import com.tg.meu_processo.dto.ProcessoCreateDTO;
import com.tg.meu_processo.dto.ProcessoDTO;
import com.tg.meu_processo.security.AuthenticatedUserService;
import com.tg.meu_processo.service.ProcessoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/processos")
@RequiredArgsConstructor
public class ProcessoController {

    private final ProcessoService service;
    private final AuthenticatedUserService authService;

    @GetMapping
    public ResponseEntity<List<ProcessoDTO>> listar() {
        if (authService.isAdmin()) {
            return ResponseEntity.ok(service.listarTodos());
        } else if (authService.isAdvogado()) {
            return ResponseEntity.ok(service.listarPorAdvogado(authService.getUsuarioId()));
        } else if (authService.isCliente()) {
            return ResponseEntity.ok(service.listarPorCliente(authService.getUsuarioId()));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProcessoDTO> buscarPorId(@PathVariable Long id) {
        ProcessoDTO processo = service.buscarPorId(id);

        if (authService.isAdmin()) {
            return ResponseEntity.ok(processo);
        }
        if (authService.isAdvogado() && processo.advogadoId().equals(authService.getUsuarioId())) {
            return ResponseEntity.ok(processo);
        }
        if (authService.isCliente() && processo.clienteId().equals(authService.getUsuarioId())) {
            return ResponseEntity.ok(processo);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ProcessoDTO> criar(@RequestBody ProcessoCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ProcessoDTO> atualizar(@PathVariable Long id, @RequestBody ProcessoCreateDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<String> deletar(@PathVariable Long id) {
        if (!service.existePorId(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Processo não encontrado");
        }
        service.deletar(id);
        return ResponseEntity.ok("Processo deletado com sucesso");
    }
}
