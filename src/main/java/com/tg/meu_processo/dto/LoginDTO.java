package com.tg.meu_processo.dto;

public record LoginDTO(String email, String senha) {}

//Cria um tipo de dado chamado LoginDTO que carrega dois valores: um email e uma senha
// record é um recurso do Java (a partir do 16) para criar classes imutáveis de forma curtíssima.
// Essa única linha gera automaticamente, sem que a gente veja as linhas de código:
//Construtor: new LoginDTO("x@y.com", "1234")
//Getters (com nome do campo): dto.email() e dto.senha()
//equals(), hashCode() e toString()
//Sem record precisaria escrever aproximadamente 30 linhas de classe tradicional para o mesmo resultado.

/*
DTO = Data Transfer Object. O papel dele é ser um "pacote" para trafegar dados entre camadas/sistemas.
 Depois de usado (autenticar), ele é descartado — não fica guardado.
 Quem guarda dados no banco é a entidade (Usuario), não o DTO.

Imutável: uma vez criado, email e senha não mudam — combina com dados que só precisam
ser lidos, como os de uma requisição.
 */