package com.tg.meu_processo.service;

import com.tg.meu_processo.dto.ProcessoCreateDTO;
import com.tg.meu_processo.entity.enums.StatusProcesso;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.util.Random;

@Service
public class ScraperSimulatorService {

    // Gerador de números aleatórios criptograficamente seguro.
    // Declarado como Random, mas instanciado como SecureRandom (subclasse)
    private final Random random = new SecureRandom();

    // Gera um ProcessoCreateDTO fictício, recebendo os ids do advogado e cliente
    public ProcessoCreateDTO simularNovoProcesso(Long advogadoId, Long clienteId) {

        String[] numerosCNJ = { // Array com números de processo no padrão CNJ para sortear
                "0001234-56.2026.8.26.0196",
                "0005678-90.2026.8.26.0050"
        };
        String[] varas = { // Array com nomes de varas para sortear
                "1ª Vara Cível",
                "2ª Vara do Trabalho"
        };

        // Monta e retorna o DTO com valores aleatórios/fixos
        return new ProcessoCreateDTO(
                numerosCNJ[random.nextInt(numerosCNJ.length)],          // sorteia um número CNJ (índice 0 ou 1)
                "Ação Trabalhista - " + random.nextInt(1000),    // título com sufixo aleatório (0 a 999)
                "Pedido de horas extras e adicional noturno",           // descricao
                StatusProcesso.EM_ANDAMENTO,
                varas[random.nextInt(varas.length)],                    // sorteia uma vara
                advogadoId,
                clienteId
        );
    }

    // Simula a coleta de movimentações de um processo (recebe o id do processo)
    // Usa nomes totalmente qualificados (caminho completo) em vez de imports
    public java.util.List<com.tg.meu_processo.dto.MovimentacaoCreateDTO> simularColeta(Long processoId) {

        // Retorna uma lista imutável com 4 movimentações fictícias
        return java.util.List.of(
                new com.tg.meu_processo.dto.MovimentacaoCreateDTO(
                        processoId,
                        "Intimado para apresentação de contrarazões",
                        java.time.LocalDateTime.now().minusDays(1) // data: 1 dia atrás
                ),
                new com.tg.meu_processo.dto.MovimentacaoCreateDTO(
                        processoId,
                        "Embargos de Declaração rejeitados",
                        java.time.LocalDateTime.now().minusDays(3)  // data: 3 dias atrás
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

//Simula um scraper gerando processos e movimentações fictícias para teste.