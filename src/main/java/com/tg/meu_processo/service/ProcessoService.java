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
@RequiredArgsConstructor
public class ProcessoService {

    private final ProcessoRepository repository;
    private final UsuarioRepository usuarioRepository;

    public List<ProcessoDTO> listarTodos() {
        return repository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public List<ProcessoDTO> listarPorAdvogado(Long advogadoId) {
        return repository.findByAdvogadoId(advogadoId).stream()
                .map(this::toDTO)
                .toList();
    }

    public List<ProcessoDTO> listarPorCliente(Long clienteId) {
        return repository.findByClienteId(clienteId).stream()
                .map(this::toDTO)
                .toList();
    }

    public ProcessoDTO buscarPorId(Long id) {
        return repository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Processo não encontrado"));
    }

    public ProcessoDTO criar(ProcessoCreateDTO dto) {
        Usuario advogado = usuarioRepository.findById(dto.advogadoId())
                .filter(u -> u.getPerfil() == PerfilUsuario.ADVOGADO)
                .orElseThrow(() -> new RuntimeException("Advogado não encontrado"));

        Usuario cliente = usuarioRepository.findById(dto.clienteId())
                .filter(u -> u.getPerfil() == PerfilUsuario.CLIENTE)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        Processo processo = new Processo();
        processo.setNumeroProcesso(dto.numeroProcesso());
        processo.setTitulo(dto.titulo());
        processo.setDescricao(dto.descricao());
        processo.setStatus(dto.status());
        processo.setVara(dto.vara());
        processo.setAdvogado(advogado);
        processo.setCliente(cliente);

        return toDTO(repository.save(processo));
    }

    public ProcessoDTO atualizar(Long id, ProcessoCreateDTO dto) {
        Processo processo = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Processo não encontrado"));

        processo.setNumeroProcesso(dto.numeroProcesso());
        processo.setTitulo(dto.titulo());
        processo.setDescricao(dto.descricao());
        processo.setStatus(dto.status());
        processo.setVara(dto.vara());

        return toDTO(repository.save(processo));
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }

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
