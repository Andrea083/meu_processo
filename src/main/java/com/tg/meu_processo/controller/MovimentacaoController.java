package com.tg.meu_processo.controller;

import com.tg.meu_processo.dto.MovimentacaoCreateDTO;
import com.tg.meu_processo.dto.MovimentacaoDTO;
import com.tg.meu_processo.service.MovimentacaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/movimentacoes")

//a classe MovimentacaoController vai receber as requisições http e delegar para service
public class MovimentacaoController {

    //injeta MovimentacaoService, responsável pela lógica, via construtor
    private final MovimentacaoService service; //campo que guarda a dependência

    public MovimentacaoController(MovimentacaoService service) { //o construtor que recebe
        this.service = service; // e atribui ao campo
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

//@PathVariable pega um valor que vem na própria URL
//@RequestBody pega os dados JSON que vêm no corpo da requisição
//ResponseEntity permite controlar a resposta HTTP completa (status, headers, corpo)
//<Void> significa que não há corpo na resposta
// Long para "qual recurso" e DTO para "quais informações".