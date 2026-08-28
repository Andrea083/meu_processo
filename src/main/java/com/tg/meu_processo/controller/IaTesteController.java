package com.tg.meu_processo.controller;

//testar uso de IA generativa Gemini como tradutor de juridiquês
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ia")
public class IaTesteController {

    private final ChatClient chatClient;

    public IaTesteController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @PostMapping ("/perguntar")
    public String perguntar(@RequestBody PerguntaRequest req) {
        return chatClient.prompt()
                .user(req.pergunta())
                .call()
                .content();
    }

    public record PerguntaRequest(String pergunta) {}
}
