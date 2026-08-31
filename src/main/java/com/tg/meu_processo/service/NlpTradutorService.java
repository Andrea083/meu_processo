package com.tg.meu_processo.service;

import org.springframework.ai.chat.client.ChatClient;  // Importa o cliente de IA generativa (Spring AI)
import org.springframework.stereotype.Service;        // Importa a anotação que marca a classe como serviço Spring
import java.util.List;

@Service // Marca a classe como um "serviço" gerenciado pelo Spring (injeção de dependência automática)
public class NlpTradutorService {

    // Dependência: dicionário local de traduções (rápido e sem custo)
    private final DicionarioJuridico dicionario;

    // Dependência: cliente que conversa com a IA generativa - Gemini gratuita para estudante -
    private final ChatClient chatClient;

    // Texto de instrução fixo enviado à IA, definindo COMO ela deve se comportar.
    // "static final" = constante única, compartilhada por toda a classe.
    private static final String SYSTEM_PROMPT = """
            Você é um assistente que traduz andamentos processuais jurídicos
            para linguagem simples, clara e acolhedora, para uma pessoa leiga.

            Regras:
            - Explique o que aconteceu, que significa na prática e qual próximo passo do processo.
            - Destaque prazos ou ações necessárias, se houver.
            - Não use jargão jurídico; se citar um termo técnico, explique-o brevemente.
            - No máximo 3 frases curtas.
            - Não invente informações que não estão no texto.
            - Responda apenas com a explicação, sem introduções.
            """;

    // Construtor: o Spring injeta as dependências aqui automaticamente.
    // Recebe o Builder do ChatClient e o "constrói" uma única vez.
    public NlpTradutorService(DicionarioJuridico dicionario, ChatClient.Builder chatClientBuilder) {
        this.dicionario = dicionario;                // Guarda o dicionário injetado
        this.chatClient = chatClientBuilder.build(); // Cria o chatClient uma única vez
    }

    // Método principal: recebe o texto jurídico e devolve a versão traduzida
    public String traduzirJuridiques(String textoOriginal) {

        //se o texto for nulo ou vazio, retorna mensagem padrão
        if (textoOriginal == null || textoOriginal.isBlank()) {
            return "Andamento sem descrição.";
        }

        // 1º) dicionário local (rápido e grátis)
        List<String> traducaoLocal = dicionario.traduzir(textoOriginal);
        if (!traducaoLocal.isEmpty()) {
            return String.join("\n", traducaoLocal);
        }

        // 2º) IA generativa usada se o dicionário não resolver
        try {
            return chatClient.prompt()           // Inicia a montagem do pedido à IA
                    .system(SYSTEM_PROMPT)      // Define as regras de comportamento da IA
                    .user("Traduza este andamento processual: " + textoOriginal)   // Envia o texto do usuário
                    .call()                     // Executa a chamada à IA
                    .content();                 // Extrai apenas o texto da resposta

        // 3º) Se a IA falhar (erro, sem conexão, etc.) retorna o original
        } catch (Exception e) {
            return "Andamento: " + textoOriginal;
        }
    }
    //método adiocionado para informar a fonte da resposta
    public record ResultadoTraducao(String fonte, String traducao) {}

    public ResultadoTraducao traduzirComFonte(String textoOriginal) {
        if (textoOriginal == null || textoOriginal.isBlank()) {
            return new ResultadoTraducao("VALIDACAO", "Andamento sem descrição.");
        }

        List<String> traducaoLocal = dicionario.traduzir(textoOriginal);
        if (!traducaoLocal.isEmpty()) {
            return new ResultadoTraducao("DICIONARIO", String.join("\n", traducaoLocal));
        }

        try {
            String resposta = chatClient.prompt()
                    .system(SYSTEM_PROMPT)
                    .user("Traduza este andamento processual: " + textoOriginal)
                    .call()
                    .content();
            return new ResultadoTraducao("IA", resposta);
        } catch (Exception e) {
            return new ResultadoTraducao("FALLBACK", "Andamento: " + textoOriginal);
        }
    }
}


