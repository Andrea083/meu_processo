package com.tg.meu_processo.dto;

import java.time.LocalDateTime;

//dados para criar uma movimentação
public record MovimentacaoCreateDTO( //create significa que a entrada vem do front
        Long processoId, // ID do processo ao qual a movimentação pertence
        String descricaoOriginal, // texto da movimentação
        LocalDateTime dataMovimentacao // data e hora do evento
) {}

//o record gera construtor, getters (dto.processoId(), dto.descricaoOriginal(),
// dto.dataMovimentacao()), equals, hashCode e toString automaticamente.
// E o Spring converte um JSON em objeto.

/*
Separar DTO de criação e de resposta é boa prática:

 CreateDTO → o que o cliente manda (só o necessário para criar).
 DTO → o que o servidor retorna (dados completos, já processados)

 */