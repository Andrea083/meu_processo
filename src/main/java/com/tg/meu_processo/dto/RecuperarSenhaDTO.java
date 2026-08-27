package com.tg.meu_processo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

//record é ideal para DTOs porque cria uma classe imutável e enxuta automaticamente
public record RecuperarSenhaDTO(
        @NotBlank @Email String email
) {}

// @NotBlank @Email para garantir que o email venha válido
/*
O RecuperarSenhaDTO é um objeto de transferência de dados que define e transporta as
informações enviadas pelo cliente ao endpoint de recuperação de senha —
neste caso, apenas o email.
Usar um DTO garante um contrato claro da API e evita expor a entidade interna do sistema
 */