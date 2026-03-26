package com.tg.meu_processo.dto;

import java.time.LocalDateTime;

public record MovimentacaoCreateDTO(
        Long processoId,
        String descricaoOriginal,
        LocalDateTime dataMovimentacao
) {}

