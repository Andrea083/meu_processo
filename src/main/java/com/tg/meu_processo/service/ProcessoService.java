package com.tg.meu_processo.service;

import com.tg.meu_processo.dto.ProcessoCreateDTO;
import com.tg.meu_processo.dto.ProcessoDTO;
import com.tg.meu_processo.entity.Processo;
import com.tg.meu_processo.entity.Usuario;
import com.tg.meu_processo.entity.enums.PerfilUsuario;
import com.tg.meu_processo.repository.ProcessoRepository;
import com.tg.meu_processo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor   //gera automaticamente um construtor
public class ProcessoService {

    //Repositórios injetados via construtor
    private final ProcessoRepository repository;
    private final UsuarioRepository usuarioRepository;

    // Verifica se existe um processo com o id informado; retorna true/false
    public boolean existePorId(Long id) {
        return repository.existsById(id);
    }

    // Lista todos os processos e converte cada entidade em DTO
    public List<ProcessoDTO> listarTodos() {
        return repository.findAll().stream() // busca todos e cria um stream
                .map(this::toDTO)            // converte cada Processo em ProcessoDTO
                .toList();                   // coleta em uma lista imutável
    }

    // Lista os processos vinculados a um advogado específico
    public List<ProcessoDTO> listarPorAdvogado(Long advogadoId) {
        return repository.findByAdvogadoId(advogadoId).stream()
                .map(this::toDTO)
                .toList();
    }

    // Lista os processos vinculados a um cliente específico
    public List<ProcessoDTO> listarPorCliente(Long clienteId) {
        return repository.findByClienteId(clienteId).stream()
                .map(this::toDTO) // se presente, converte em DTO
                .toList();
    }

    // Busca um processo por id; lança exceção se não encontrar
    public ProcessoDTO buscarPorId(Long id) {
        return repository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Processo não encontrado"));
    }

    // Cria um novo processo a partir dos dados recebidos (DTO de criação)
    public ProcessoDTO criar(ProcessoCreateDTO dto) {
        // Busca o usuário e valida o perfil
        Usuario advogado = usuarioRepository.findById(dto.advogadoId())
                .filter(u -> u.getPerfil() == PerfilUsuario.ADVOGADO)
                .orElseThrow(() -> new RuntimeException("Advogado não encontrado"));

        Usuario cliente = usuarioRepository.findById(dto.clienteId())
                .filter(u -> u.getPerfil() == PerfilUsuario.CLIENTE)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        // Cria a entidade e preenche os campos com os dados do DTO
        Processo processo = new Processo();
        processo.setNumeroProcesso(dto.numeroProcesso());
        processo.setTitulo(dto.titulo());
        processo.setDescricao(dto.descricao());
        processo.setStatus(dto.status());
        processo.setVara(dto.vara());
        processo.setAdvogado(advogado);
        processo.setCliente(cliente);

        // Salva no banco e retorna o resultado já convertido em DTO
        return toDTO(repository.save(processo));
    }

    // Atualiza um processo existente identificado pelo id
    public ProcessoDTO atualizar(Long id, ProcessoCreateDTO dto) {
        Processo processo = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Processo não encontrado"));

        // Atualiza apenas os campos de dados (não altera advogado/cliente)
        processo.setNumeroProcesso(dto.numeroProcesso());
        processo.setTitulo(dto.titulo());
        processo.setDescricao(dto.descricao());
        processo.setStatus(dto.status());
        processo.setVara(dto.vara());

        // Salva no banco e retorna o DTO atualizado
        return toDTO(repository.save(processo));
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }

    // Método auxiliar: converte a entidade Processo em ProcessoDTO
    private ProcessoDTO toDTO(Processo p) {
        return new ProcessoDTO(
                p.getId(),
                p.getNumeroProcesso(),
                p.getTitulo(),
                p.getDescricao(),
                p.getStatus(),
                p.getVara(),
                p.getAdvogado().getId(),
                p.getAdvogado().getNome(),
                p.getCliente().getId(),
                p.getCliente().getNome()
        );
    }

}

//DTO = padrão de projetos para transportar dados entre camadas