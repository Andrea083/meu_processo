package com.tg.meu_processo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Meu Processo")
                        .version("v1")
                        .description("API para Acompanhamento de Processos Judiciais.")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }
}
// O SpringDoc gera uma interface visual (Swagger UI, geralmente em /swagger-ui.html)
// onde pode ver e testar todos os endpoints do backend —
// captura de dados da AASP, tradução via NLP, arquivamento nos perfis, etc. —
// sem precisar de Postman, Insomnia ou similar.