package com.tg.meu_processo.dto;

import java.time.LocalDateTime;

//dados devolvidos

public record MovimentacaoDTO(
        Long id, //Gerado pelo banco, só existe após salvar
        Long processoId,
        String descricaoOriginal,
        String descricaoTraduzida, //Preenchido pelo sistema depois
        LocalDateTime dataMovimentacao,
        Boolean notificacaoEnviada // true/false: se a notificação já foi enviada. Status controlado pelo back
) {}

/*
Separar DTO de criação e de resposta é boa prática:

 CreateDTO → o que o cliente manda (só o necessário para criar).
 DTO → o que o servidor retorna (dados completos, já processados)

 */