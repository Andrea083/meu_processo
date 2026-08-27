package com.tg.meu_processo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

//@Entity / @Table → mapeia a classe para a tabela
@Entity
@Table(name = "movimentacoes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movimentacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String descricaoOriginal;

    @Column(columnDefinition = "TEXT")
    private String descricaoTraduzida;

    @Column(nullable = false)
    private LocalDateTime dataMovimentacao;

    @ManyToOne //relacionamentos
    @JoinColumn(name = "processo_id", nullable = false)
    private Processo processo;

    private Boolean notificacaoEnviada;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        if (notificacaoEnviada == null) notificacaoEnviada = false;
    }
}

//entidade JPA que define o mapeamento entre a classe Java e a tabela movimentacoes
// esta classe descreve como os dados serão guardados
//@PrePersist onCreate() → só executa no momento em que um registro for salvo, preenchendo dataCriacao.