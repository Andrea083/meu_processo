//camada de serviço (regras de negócio) para as movimentações de processos jurídicos.
// faz o CRUD (criar, listar, buscar, deletar) e orquestra um pipeline que simula
// coleta automática + tradução de "juridiquês" via IA.
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
    private final MovimentacaoRepository repository;        // acesso ao banco das movimentações
    private final ProcessoRepository processoRepository;    // acesso ao banco dos processos
    private final NlpTradutorService nlpTradutor;           // serviço que "traduz" texto jurídico (IA)
    private final ScraperSimulatorService scraperSimulator; // simula a coleta de movimentações

    //lista movimentações de um processo
    public List<MovimentacaoDTO> listarPorProcesso(Long processoId) {
        return repository.findByProcessoIdOrderByDataMovimentacaoDesc(processoId).stream()
                .map(this::toDTO)     // converte cada entidade em DTO
                .toList();
    } //Busca todas as movimentações do processo, ordenadas pela data (mais recente primeiro), e converte cada uma em MovimentacaoDTO

    public MovimentacaoDTO buscarPorId(Long id) {
        return repository.findById(id)
                .map(this::toDTO)     // se achou, vira DTO
                .orElseThrow(() -> new RuntimeException("Movimentação não encontrada"));
    }

    //cria uma movimentação manualmente
    public MovimentacaoDTO criar(MovimentacaoCreateDTO dto) {
        Movimentacao mov = new Movimentacao();              // nova entidade
        mov.setDescricaoOriginal(dto.descricaoOriginal());   // texto original
        mov.setDataMovimentacao(dto.dataMovimentacao());
        mov.setNotificacaoEnviada(false);                    // ainda não notificou
        mov.setProcesso(processoRepository.findById(dto.processoId()) // vincula ao processo
                .orElseThrow(() -> new RuntimeException("Processo não encontrado")));
        return toDTO(repository.save(mov));                 // salva e retorna como DTO
    }

    //remove por id
    public void deletar(Long id) {
        repository.deleteById(id);
    }

    //Pipeline completo (coleta + tradução)
    //Para cada item coletado, monta a entidade e vincula ao processo (igual ao criar)
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

                    //chama a IA para traduzir o texto jurídico em linguagem acessível.
                    mov.setDescricaoTraduzida(nlpTradutor.traduzirJuridiques(dto.descricaoOriginal()));

                    repository.save(mov);  // persiste
                    return toDTO(mov);     // retorna como dto
                })
                .toList();
    }           //Salva cada uma e devolve a lista já traduzida

    //conversão entidade → DTO (privado)
    private MovimentacaoDTO toDTO(Movimentacao m) {
        return new MovimentacaoDTO(
                m.getId(), m.getProcesso().getId(),
                m.getDescricaoOriginal(), m.getDescricaoTraduzida(),
                m.getDataMovimentacao(), m.getNotificacaoEnviada()
                //Expõe apenas os campos necessários para a API, sem devolver a entidade inteira
                // boa prática — evita vazar detalhes internos e problemas de serialização lazy)
        );
    }
}
