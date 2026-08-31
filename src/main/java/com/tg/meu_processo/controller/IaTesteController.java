package com.tg.meu_processo.controller;

//testar uso de IA generativa Gemini como tradutor de juridiquês
import com.tg.meu_processo.service.NlpTradutorService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;

@RestController          //cada método retorna diretamente o corpo da resposta HTTP (JSON/texto), sem view.
@RequestMapping("/ia")   //define prefixo de rota da classe. Todos os endpoints começam com /ia.
public class IaTesteController {

    private final ChatClient chatClient;   //injetadas via construtor
    private final NlpTradutorService tradutorService;

    public IaTesteController(ChatClient.Builder builder, NlpTradutorService tradutorService) {
        this.chatClient = builder.build();   //chama build para criar o cliente
        this.tradutorService = tradutorService;  //injeta tradução
    }

    @PostMapping("/perguntar")
    public String perguntar(@RequestBody TextoRequest req) { // converte o JSON recebido em um objeto TextoRequest.
        return chatClient.prompt()
                .user(req.texto())   //define a mensagem do usuário
                .call()             //envia ao modelo de IA
                .content();         //extrai a resposta como texto (retornado ao cliente)
    }

    //Recebe o texto e delega ao serviço, que retorna um ResultadoTraducao (serializado como JSON).
    @PostMapping("/traduzir")
    public NlpTradutorService.ResultadoTraducao traduzir(@RequestBody TextoRequest req) {
        return tradutorService.traduzirComFonte(req.texto());
    }

    //DTO de entrada. Um record imutável com o campo texto, usado no @RequestBody de ambos os endpoints.
    public record TextoRequest(String texto) {
    }
}

// ia/perguntar manda a pergunta pro modelo de IA e devolve a resposta;
// ia/traduzir traduz o texto via serviço interno.