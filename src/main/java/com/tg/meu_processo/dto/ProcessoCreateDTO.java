package com.tg.meu_processo.dto;

import com.tg.meu_processo.entity.enums.StatusProcesso;

public record ProcessoCreateDTO(
        String numeroProcesso,
        String titulo,
        String descricao,
        StatusProcesso status,
        String vara,
        String comarca,
        Long advogadoId,
        Long clienteId
) {}

