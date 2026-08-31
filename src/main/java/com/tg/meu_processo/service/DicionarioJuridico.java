package com.tg.meu_processo.service;

import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;

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

    // Normaliza o texto de entrada (minúsculas, sem acentos, espaços colapsados)
    // para que a comparação não falhe por diferenças de formatação.
    public List<String> traduzir(String textoOriginal) {
        String textoLower = normalizar(textoOriginal);

        // Percorre todas as entradas do dicionário (chave = termo jurídico, valor = explicação).
        return termos.entrySet().stream()
                // Mantém apenas as entradas cujo termo (normalizado) aparece dentro do texto.
                .filter(e -> textoLower.contains(normalizar(e.getKey())))

                // Ordena por tamanho do termo, do maior para o menor:
                // termos longos são mais específicos e devem vir antes dos genéricos.
                .sorted(Comparator.comparingInt((Map.Entry<String, String> e) -> e.getKey().length()).reversed())

                // Descarta a chave e mantém apenas o valor (a explicação em linguagem comum).
                .map(Map.Entry::getValue)

                // Junta os resultados numa lista
                .collect(Collectors.toList());
    }

    // Padroniza o texto para tornar a comparação confiável.
    private String normalizar(String texto) {

        // NFD separa cada letra acentuada em letra base + marca de acento (ex.: "á" -> "a" + "´").
        return Normalizer.normalize(texto.toLowerCase().trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")      // remove acentos
                .replaceAll("\\s+", " ");       // troca qualquer sequência de espaços/quebras por um único espaço
    }
}
