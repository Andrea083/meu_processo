package com.tg.meu_processo.repository;

import com.tg.meu_processo.entity.Movimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

//Declara o repositório da entidade Movimentacao
public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long> {
    List<Movimentacao> findByProcessoIdOrderByDataMovimentacaoDesc(Long processoId);
    List<Movimentacao> findByNotificacaoEnviadaFalse(); //Retorna todas as movimentações cuja notificação ainda não foi enviada
}

//O Spring Data JPA gera a implementação automaticamente em tempo de execução
// (via proxy dinâmico). Você só declara o contrato (o que quer fazer),
// e o Spring cria o código que acessa o banco

/*
findBy → select
ProcessoId → filtra por id
OrderByDataMovimentacaoDesc → ordena decrescente
 */