package com.tg.meu_processo.service;

import com.tg.meu_processo.dto.ProcessoCreateDTO;
import com.tg.meu_processo.entity.enums.StatusProcesso;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.util.Random;

@Service
public class ScraperSimulatorService {
    private final Random random = new SecureRandom();

    // ✅ CORRIGIDO: Agora retorna ProcessoCreateDTO completo com 7 parâmetros
    public ProcessoCreateDTO simularNovoProcesso(Long advogadoId, Long clienteId) {
        String[] numerosCNJ = {
                "0001234-56.2026.8.26.0196",
                "0005678-90.2026.8.26.0050"
        };
        String[] varas = {
                "1ª Vara Cível",
                "2ª Vara do Trabalho"
        };

        return new ProcessoCreateDTO(
                numerosCNJ[random.nextInt(numerosCNJ.length)],  // numeroProcesso
                "Ação Trabalhista - " + random.nextInt(1000),    // titulo
                "Pedido de horas extras e adicional noturno",    // descricao
                StatusProcesso.EM_ANDAMENTO,                      // status
                varas[random.nextInt(varas.length)],             // vara
                advogadoId,                                       // ← ADICIONADO
                clienteId                                         // ← ADICIONADO
        );
    }

    // Simula coleta de movimentações
    public java.util.List<com.tg.meu_processo.dto.MovimentacaoCreateDTO> simularColeta(Long processoId) {
        return java.util.List.of(
                new com.tg.meu_processo.dto.MovimentacaoCreateDTO(
                        processoId,
                        "Intimado para apresentação de contrarazões",
                        java.time.LocalDateTime.now().minusDays(1)
                ),
                new com.tg.meu_processo.dto.MovimentacaoCreateDTO(
                        processoId,
                        "Embargos de Declaração rejeitados",
                        java.time.LocalDateTime.now().minusDays(3)
                ),
                new com.tg.meu_processo.dto.MovimentacaoCreateDTO(
                        processoId,
                        "Citado por edital",
                        java.time.LocalDateTime.now().minusDays(7)
                ),
                new com.tg.meu_processo.dto.MovimentacaoCreateDTO(
                        processoId,
                        "Deferida tutela de urgência",
                        java.time.LocalDateTime.now().minusDays(15)
                )
        );
    }
}
