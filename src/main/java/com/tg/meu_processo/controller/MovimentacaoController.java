package com.tg.meu_processo.controller;

import com.tg.meu_processo.dto.MovimentacaoCreateDTO;
import com.tg.meu_processo.dto.MovimentacaoDTO;
import com.tg.meu_processo.service.MovimentacaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/movimentacoes")
public class MovimentacaoController {

    private final MovimentacaoService service;

    public MovimentacaoController(MovimentacaoService service) {
        this.service = service;
    }

    @PostMapping("/simular/{processoId}")
    public List<MovimentacaoDTO> simularColeta(@PathVariable Long processoId) {
        return service.simularPipelineCompleto(processoId);
    }

    @GetMapping("/processo/{processoId}")
    public List<MovimentacaoDTO> listarPorProcesso(@PathVariable Long processoId) {
        return service.listarPorProcesso(processoId);
    }

    @GetMapping("/{id}")
    public MovimentacaoDTO buscar(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    public MovimentacaoDTO criar(@RequestBody MovimentacaoCreateDTO dto) {
        return service.criar(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}

