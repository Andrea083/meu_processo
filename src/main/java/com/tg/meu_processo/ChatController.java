package com.tg.meu_processo;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
class ChatController {
    private final ChatClient chat;

    ChatController(ChatClient.Builder builder) {
        this.chat = builder.build();
    }

    @GetMapping("/chat")
    String chat(@RequestParam String msg) {
        return chat.prompt().user(msg).call().content();
        // inicia a construção do prompt / define a mensagem do usuário / envia ao modelo de IA / extrai o texto da resposta
    }
}

//controller REST que expõe um endpoint de chat com IA usando Spring AI
/*
 @RestController → classe que responde requisições HTTP retornando dados direto no corpo da resposta (não view/HTML).
 private final ChatClient chat; → cliente de IA generativa que faz a chamada ao modelo.
 Construtor → recebe um ChatClient.Builder (injetado pelo Spring), chama build() uma vez e guarda o cliente pronto. Boa prática de performance.
 @GetMapping("/chat") → mapeia requisições GET na URL /chat.
 @RequestParam String msg → captura o parâmetro da URL. Ex: /chat?msg=ola.
 */
