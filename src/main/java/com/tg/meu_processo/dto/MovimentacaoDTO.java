package com.tg.meu_processo.dto;

import java.time.LocalDateTime;

public record MovimentacaoDTO(
        Long id,
        Long processoId,
        String descricaoOriginal,
        String descricaoTraduzida,
        LocalDateTime dataMovimentacao,
        Boolean notificacaoEnviada
) {}

