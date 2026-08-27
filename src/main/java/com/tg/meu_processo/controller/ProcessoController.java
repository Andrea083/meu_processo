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

@RestController                   //expõe endpoints REST (retorna dados, não páginas)
@RequestMapping("/api/processos") //rota base de todos os métodos
@RequiredArgsConstructor          //Lombok gera o construtor com os campos final (injeção)
public class ProcessoController {

    private final ProcessoService service; //// lógica de negócio dos processos
    private final AuthenticatedUserService authService; //// identifica o usuário logado e seu perfil

    @GetMapping
    public ResponseEntity<List<ProcessoDTO>> listar() { //busca processos por perfil e id
        if (authService.isAdmin()) {
            return ResponseEntity.ok(service.listarTodos()); //admin pode ver todos
        } else if (authService.isAdvogado()) {
            return ResponseEntity.ok(service.listarPorAdvogado(authService.getUsuarioId())); //advogado só os lincados ao id
        } else if (authService.isCliente()) {
            return ResponseEntity.ok(service.listarPorCliente(authService.getUsuarioId())); //cliente só vê os lincados ao seu id
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); //se não for nenhum dos 3, erro 403 forbidden
    }

    @GetMapping("/{id}") //busca processos por id
    public ResponseEntity<ProcessoDTO> buscarPorId(@PathVariable Long id) { //@PathVariable identifica o id na url
        ProcessoDTO processo = service.buscarPorId(id);

        if (authService.isAdmin()) {
            return ResponseEntity.ok(processo); //se admin, vê todos
        }
        if (authService.isAdvogado() && processo.advogadoId().equals(authService.getUsuarioId())) {
            return ResponseEntity.ok(processo); //se o usuário logado é advogado e o id do advogado dono do processo é igual ao id do usuário logado, então retorna o processo.
        }
        if (authService.isCliente() && processo.clienteId().equals(authService.getUsuarioId())) {
            return ResponseEntity.ok(processo);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // usuário logado não é dono do processo, não vê
    }

    //ações realizadas apenas no perfil de administrador
    @PostMapping //Post - inserir (criar) processo
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ProcessoDTO> criar(@RequestBody ProcessoCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @PutMapping("/{id}") //Put para alterar, atualizar. Confere id, exige inserção de dados no body
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

//controlador de processos com controle de acesso por perfil (admin, advogado, cliente)
/*
@PreAuthorize("hasRole('ADMINISTRADOR')") → segurança declarativa: o Spring bloqueia antes de entrar no método (usado em criar/atualizar/deletar).
if (authService.isAdmin())... → segurança programática/manual: checa dentro do código porque precisa filtrar dados por perfil, não só bloquear (usado em listar/buscar).
Ambos se complementam: escrita é exclusiva do admin; leitura é liberada, mas com dados restritos ao dono.
 */