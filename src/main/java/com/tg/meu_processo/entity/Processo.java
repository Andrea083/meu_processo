package com.tg.meu_processo.entity;

import com.tg.meu_processo.entity.enums.StatusProcesso;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

//mapeia a classe para a tabela
@Entity
@Table(name = "processos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Processo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String numeroProcesso;

    @Column(length = 255)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(length = 100)
    private String vara;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusProcesso status;

    @ManyToOne // relacionamento: muitos processos podem ter o mesmo cliente
    @JoinColumn(name = "cliente_id", nullable = false) //cria a coluna cliente_id nesta tabela, que guarda o id do usuário-cliente.
    private Usuario cliente;

    @ManyToOne
    @JoinColumn(name = "advogado_id", nullable = false)
    private Usuario advogado;

    @OneToMany(mappedBy = "processo", cascade = CascadeType.ALL) //define Um processo para Várias movimentações
    private List<Movimentacao> movimentacoes;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    private LocalDateTime dataAtualizacao;

    @PrePersist //só executa no momento em que um registro for salvo
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        if (status == null) status = StatusProcesso.EM_ANDAMENTO;
    }

    //callback de ciclo de vida do JPA que atualiza automaticamente a data sempre que a entidade é modificada
    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }
}

//nullable = false -> o processo obrigatoriamente precisa ter um cliente
/*
 Usuario representa pessoas em papéis diferentes. O mesmo modelo de tabela serve tanto
 para clientes quanto para advogados — o que muda é a função no processo.
 Por isso o @JoinColumn com nomes distintos é essencial: sem ele, o JPA não saberia diferenciar
 as duas colunas e daria conflito.
 */

//@PreUpdate
//Marca o método para ser executado automaticamente pelo JPA, imediatamente antes de um
//UPDATE ser enviado ao banco. Você não chama esse método — o próprio JPA/Hibernate dispara.
//@PrePersist e @PreUpdate permitem controle automático de quando foi criado e quando
// foi atualizado pela última vez, um padrão de auditoria muito usado