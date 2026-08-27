package com.tg.meu_processo.dto;

import com.tg.meu_processo.entity.enums.StatusProcesso; //só aceita as opções declaradas

//dados para criar um processo
public record ProcessoCreateDTO(
        String numeroProcesso,
        String titulo,
        String descricao,
        StatusProcesso status,
        String vara,
        //String comarca,
        Long advogadoId,
        Long clienteId
) {}

/*
Um enum (enumeração) é um tipo que só aceita um conjunto fixo e pré-definido de valores.
 Em vez de deixar o campo status receber qualquer texto livre (o que abriria margem
 para erro), ele só aceita as opções declaradas.
 O front envia o status como texto, mas ele precisa bater exatamente com um dos valores do enum
*/