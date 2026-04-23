package com.tg.meu_processo.service;

import com.tg.meu_processo.dto.MovimentacaoCreateDTO;
import com.tg.meu_processo.dto.MovimentacaoDTO;
import com.tg.meu_processo.entity.Movimentacao;
import com.tg.meu_processo.repository.MovimentacaoRepository;
import com.tg.meu_processo.repository.ProcessoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor  // ← Lombok gera construtor automaticamente
public class MovimentacaoService {
    private final MovimentacaoRepository repository;
    private final ProcessoRepository processoRepository;
    private final NlpTradutorService nlpTradutor;
    private final ScraperSimulatorService scraperSimulator;

    public List<MovimentacaoDTO> listarPorProcesso(Long processoId) {
        return repository.findByProcessoIdOrderByDataMovimentacaoDesc(processoId).stream()
                .map(this::toDTO)
                .toList();
    }

    public MovimentacaoDTO buscarPorId(Long id) {
        return repository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Movimentação não encontrada"));
    }

    public MovimentacaoDTO criar(MovimentacaoCreateDTO dto) {
        Movimentacao mov = new Movimentacao();
        mov.setDescricaoOriginal(dto.descricaoOriginal());
        mov.setDataMovimentacao(dto.dataMovimentacao());
        mov.setNotificacaoEnviada(false);
        mov.setProcesso(processoRepository.findById(dto.processoId())
                .orElseThrow(() -> new RuntimeException("Processo não encontrado")));
        return toDTO(repository.save(mov));
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }

    //Pipeline completo (coleta + tradução)
    public List<MovimentacaoDTO> simularPipelineCompleto(Long processoId) {
        List<MovimentacaoCreateDTO> movimentacoesRaw = scraperSimulator.simularColeta(processoId);

        return movimentacoesRaw.stream()
                .map(dto -> {
                    Movimentacao mov = new Movimentacao();
                    mov.setDescricaoOriginal(dto.descricaoOriginal());
                    mov.setDataMovimentacao(dto.dataMovimentacao());
                    mov.setNotificacaoEnviada(false);
                    mov.setProcesso(processoRepository.findById(dto.processoId())
                            .orElseThrow(() -> new RuntimeException("Processo não encontrado")));

                    //TRADUÇÃO IA
                    mov.setDescricaoTraduzida(nlpTradutor.traduzirJuridiques(dto.descricaoOriginal()));

                    repository.save(mov);
                    return toDTO(mov);
                })
                .toList();
    }

    private MovimentacaoDTO toDTO(Movimentacao m) {
        return new MovimentacaoDTO(
                m.getId(), m.getProcesso().getId(),
                m.getDescricaoOriginal(), m.getDescricaoTraduzida(),
                m.getDataMovimentacao(), m.getNotificacaoEnviada()
        );
    }
}
