package com.tg.meu_processo.service;

import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class DicionarioJuridico {

    private final Map<String, String> termos = new HashMap<>();

    public DicionarioJuridico() {
        // === CITAÇÕES E INTIMAÇÕES ===
        termos.put("Intimado para apresentação de contrarazões",
                "Você foi chamado para enviar sua resposta/defesa no prazo indicado. Seu advogado é quem fará isso");
        termos.put("Citado por edital",
                "Você foi chamado oficialmente pelo jornal porque não foi encontrado presencialmente");
        termos.put("Citação eletrônica",
                "Você foi notificado por via eletrônica (email ou sistema)");
        termos.put("Intimado",
                "Você recebeu uma notificação oficial do tribunal");

        // === DECISÕES FAVORÁVEIS ===
        termos.put("Deferida tutela de urgência",
                "O juiz concordou em proteger seus direitos imediatamente, sem esperar o fim do processo");
        termos.put("Deferido", "O juiz aprovou o que você pediu");
        termos.put("Concedida liminar", "O juiz deu proteção rápida ao seu direito");

        // === DECISÕES DESFAVORÁVEIS ===
        termos.put("Embargos de Declaração rejeitados",
                "Seu pedido para o juiz esclarecer a decisão foi negado");
        termos.put("Indeferido", "O juiz negou o que você pediu");
        termos.put("Recurso negado", "O tribunal não aceitou seu pedido de revisão");

        // === DOCUMENTAÇÃO E JUNTADAS ===
        termos.put("Juntada de AR",
                "A confirmação de entrega da carta chegou e foi anexada ao processo");
        termos.put("AR positivo", "A carta foi entregue com sucesso para a pessoa");
        termos.put("AR negativo",
                "A carta não foi entregue — a pessoa recusou ou não estava no endereço");
        termos.put("Juntada de documentos", "Novos documentos foram adicionados ao processo");

        // === PRAZOS E ANDAMENTOS ===
        termos.put("Aguardando recurso",
                "Aguardando prazo para que a outra parte possa recorrer");
        termos.put("Vencido prazo", "O prazo para ação terminou");
        termos.put("Suspenso", "O processo foi pausado temporariamente");
        termos.put("Arquivado", "O processo foi encerrado e guardado nos registros");

        // === AUDIÊNCIAS E COMPARECIMENTOS ===
        termos.put("Audiência marcada", "Data agendada para você comparecer ao tribunal");
        termos.put("Comparecimento obrigatório", "Você DEVE estar presente na data marcada");
        termos.put("Ausência justificada", "Você não foi, mas apresentou motivo válido");

        // === SENTENÇAS E FINALIZAÇÕES ===
        termos.put("Sentença proferida", "O juiz deu sua decisão final sobre o caso");
        termos.put("Processo encerrado", "O caso terminou");
        termos.put("Transitado em julgado",
                "A decisão é final e não pode mais ser mudada");

        // === OUTROS TERMOS COMUNS ===
        termos.put("Petição", "Documento oficial que você envia ao tribunal");
        termos.put("Manifestação processual", "Documento que você envia ao tribunal");
        termos.put("Distribuição", "O processo foi entregue a um juiz");
        termos.put("Comarca", "Região que o tribunal atende");
        termos.put("Vara", "Setor específico do tribunal (Trabalhista, Cível, etc)");
        termos.put("Classe processual", "Tipo de processo (ação, recurso, etc)");
    }

    public Optional<String> traduzir(String textoOriginal) {
        // Busca exata
        if (termos.containsKey(textoOriginal)) {
            return Optional.of(termos.get(textoOriginal));
        }
        // Busca parcial
        for (Map.Entry<String, String> entrada : termos.entrySet()) {
            if (textoOriginal.toLowerCase().contains(entrada.getKey().toLowerCase())) {
                return Optional.of(entrada.getValue());
            }
        }
        return Optional.empty();
    }
}
