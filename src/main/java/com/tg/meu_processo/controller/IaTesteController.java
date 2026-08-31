package com.tg.meu_processo.controller;

//testar uso de IA generativa Gemini como tradutor de juridiquês
import com.tg.meu_processo.service.NlpTradutorService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ia")
public class IaTesteController {

    private final ChatClient chatClient;
    private final NlpTradutorService tradutorService;

    public IaTesteController(ChatClient.Builder builder, NlpTradutorService tradutorService) {
        this.chatClient = builder.build();
        this.tradutorService = tradutorService;
    }

    @PostMapping("/perguntar")
    public String perguntar(@RequestBody TextoRequest req) {
        return chatClient.prompt()
                .user(req.texto())
                .call()
                .content();
    }

    @PostMapping("/traduzir")
    public NlpTradutorService.ResultadoTraducao traduzir(@RequestBody TextoRequest req) {
        return tradutorService.traduzirComFonte(req.texto());
    }

    public record TextoRequest(String texto) {
    }
}
