package com.tg.meu_processo.service;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class NlpTradutorService {

    /**
     * Traduz termos jurídicos complexos para linguagem simples e acessível ao leigo.
     * Remove jargão processual completamente.
     */
    public String traduzirJuridiques(String textoOriginal) {

        // HashMap com MAPEAMENTO COMPLETO (original → tradução para leigo)
        Map<String, String> dicionarioJuridico = new HashMap<>();

        // === CITAÇÕES E INTIMAÇÕES ===
        dicionarioJuridico.put("Intimado para apresentação de contrarazões",
                "Você foi chamado para enviar sua resposta/defesa no prazo indicado. Seu advogado é quem fará isso");
        dicionarioJuridico.put("Citado por edital",
                "Você foi chamado oficialmente pelo jornal porque não foi encontrado presencialmente");
        dicionarioJuridico.put("Citação eletrônica",
                "Você foi notificado por via eletrônica (email ou sistema)");
        dicionarioJuridico.put("Intimado",
                "Você recebeu uma notificação oficial do tribunal");

        // === DECISÕES FAVORÁVEIS ===
        dicionarioJuridico.put("Deferida tutela de urgência",
                "O juiz concordou em proteger seus direitos imediatamente, sem esperar o fim do processo");
        dicionarioJuridico.put("Deferido",
                "O juiz aprovou o que você pediu");
        dicionarioJuridico.put("Concedida liminar",
                "O juiz deu proteção rápida ao seu direito");

        // === DECISÕES DESFAVORÁVEIS ===
        dicionarioJuridico.put("Embargos de Declaração rejeitados",
                "Seu pedido para o juiz esclarecer a decisão foi negado");
        dicionarioJuridico.put("Indeferido",
                "O juiz negou o que você pediu");
        dicionarioJuridico.put("Recurso negado",
                "O tribunal não aceitou seu pedido de revisão");

        // === DOCUMENTAÇÃO E JUNTADAS ===
        dicionarioJuridico.put("Juntada de AR",
                "A confirmação de entrega da carta chegou e foi anexada ao processo");
        dicionarioJuridico.put("AR positivo",
                "A carta foi entregue com sucesso para a pessoa");
        dicionarioJuridico.put("AR negativo",
                "A carta não foi entregue — a pessoa recusou ou não estava no endereço");
        dicionarioJuridico.put("Juntada de documentos",
                "Novos documentos foram adicionados ao processo");

        // === PRAZOS E ANDAMENTOS ===
        dicionarioJuridico.put("Aguardando recurso",
                "Aguardando prazo para que a outra parte possa recorrer");
        dicionarioJuridico.put("Vencido prazo",
                "O prazo para ação terminou");
        dicionarioJuridico.put("Suspenso",
                "O processo foi pausado temporariamente");
        dicionarioJuridico.put("Arquivado",
                "O processo foi encerrado e guardado nos registros");

        // === AUDIÊNCIAS E COMPARECIMENTOS ===
        dicionarioJuridico.put("Audiência marcada",
                "Data agendada para você comparecer ao tribunal");
        dicionarioJuridico.put("Comparecimento obrigatório",
                "Você DEVE estar presente na data marcada");
        dicionarioJuridico.put("Ausência justificada",
                "Você não foi, mas apresentou motivo válido");

        // === SENTENÇAS E FINALIZAÇÕES ===
        dicionarioJuridico.put("Sentença proferida",
                "O juiz deu sua decisão final sobre o caso");
        dicionarioJuridico.put("Processo encerrado",
                "O caso terminou");
        dicionarioJuridico.put("Transitado em julgado",
                "A decisão é final e não pode mais ser mudada");

        // === OUTROS TERMOS COMUNS ===
        dicionarioJuridico.put("Petição",
                "Documento oficial que você envia ao tribunal");
        dicionarioJuridico.put("Manifestação processual",
                "Documento que você envia ao tribunal");
        dicionarioJuridico.put("Distribuição",
                "O processo foi entregue a um juiz");
        dicionarioJuridico.put("Comarca",
                "Região que o tribunal atende");
        dicionarioJuridico.put("Vara",
                "Setor específico do tribunal (Trabalhista, Cível, etc)");
        dicionarioJuridico.put("Classe processual",
                "Tipo de processo (ação, recurso, etc)");

        // Busca exata primeiro
        if (dicionarioJuridico.containsKey(textoOriginal)) {
            return dicionarioJuridico.get(textoOriginal);
        }

        // Se não encontrar exata, tenta parcial (para variações)
        for (Map.Entry<String, String> entrada : dicionarioJuridico.entrySet()) {
            if (textoOriginal.toLowerCase().contains(entrada.getKey().toLowerCase())) {
                return entrada.getValue();
            }
        }

        // Fallback: retorna original se não conseguir traduzir
        return "Andamento: " + textoOriginal;
    }
}
