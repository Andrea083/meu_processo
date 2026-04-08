package com.tg.meu_processo.dto;

import com.tg.meu_processo.entity.enums.PerfilUsuario;
import jakarta.validation.constraints.NotBlank;

public record UsuarioCreateDTO(
        @NotBlank(message = "Nome é obrigatório")
        String nome,

        @NotBlank(message = "CPF é obrigatório")
        String cpf,

        String cargo,
        String oab,

        @NotBlank(message = "Email é obrigatório")
        String email,

        String whatsapp,
        PerfilUsuario perfil
) {}