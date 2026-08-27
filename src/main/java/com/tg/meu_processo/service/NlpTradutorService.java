package com.tg.meu_processo.service;

import com.tg.meu_processo.service.DicionarioJuridico; // Importa o dicionário de termos jurídicos
import org.springframework.ai.chat.client.ChatClient;  // Importa o cliente de IA generativa (Spring AI)
import org.springframework.stereotype.Service;        // Importa a anotação que marca a classe como serviço Spring
import java.util.Optional; //usado para valores que podem ou não existir

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
            - Explique o que aconteceu e o que significa na prática.
            - Destaque prazos ou ações necessárias, se houver.
            - Não use jargão jurídico; se citar um termo técnico, explique-o.
            - No máximo 3 frases curtas.
            - Não invente informações que não estão no texto.
            - Responda apenas com a explicação, sem introduções.
            """;

    // Construtor: o Spring injeta as dependências aqui automaticamente.
    // Recebe o Builder do ChatClient e o "constrói" uma única vez (mais eficiente).
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
        Optional<String> traducaoLocal = dicionario.traduzir(textoOriginal);
        if (traducaoLocal.isPresent()) {
            return traducaoLocal.get();
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
}


