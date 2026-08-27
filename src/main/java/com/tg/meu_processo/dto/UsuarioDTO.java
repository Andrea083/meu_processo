package com.tg.meu_processo.dto;

import com.tg.meu_processo.entity.enums.PerfilUsuario; //só perfis declarados

//dados devolvidos
public record UsuarioDTO(
        Long id,
        String nome,
        String cpf,
        String cargo,
        String oab,
        String email,
        String whatsapp,
        PerfilUsuario perfil
) {}



