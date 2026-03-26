package com.tg.meu_processo.dto;

import com.tg.meu_processo.entity.enums.StatusProcesso;

public record ProcessoDTO(
        Long id,
        String numeroProcesso,
        String titulo,
        String descricao,
        StatusProcesso status,
        String vara,
        //String comarca,
        Long advogadoId,
        String advogadoNome,
        Long clienteId,
        String clienteNome
) {}
