package com.tg.meu_processo.entity;

import com.tg.meu_processo.entity.enums.PerfilUsuario;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

//mapeia a classe para a tabela
@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, unique = true, length = 14)
    private String cpf;

    @Column(length = 50)
    private String cargo; // usado apenas para ADMIN

    @Column(length = 20)
    private String oab; // usado apenas para ADVOGADO

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 20)
    private String whatsapp;

    @Column(nullable = false)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PerfilUsuario perfil;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    private LocalDateTime dataAtualizacao;

    @PrePersist //só executa no momento em que um registro for salvo
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
    }
    //atualiza automaticamente a data sempre que a entidade é modificada
    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }
}

/*
@PreUpdate marca o método para ser executado automaticamente pelo JPA, imediatamente antes de um
UPDATE ser enviado ao banco. Você não chama esse método — o próprio JPA/Hibernate dispara.
@PrePersist e @PreUpdate permitem controle automático de quando foi criado e quando
foi atualizado pela última vez, um padrão de auditoria muito usado
*/