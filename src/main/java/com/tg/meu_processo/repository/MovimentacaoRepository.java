package com.tg.meu_processo.repository;

import com.tg.meu_processo.entity.Movimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long> {
    List<Movimentacao> findByProcessoIdOrderByDataMovimentacaoDesc(Long processoId);
    List<Movimentacao> findByNotificacaoEnviadaFalse();
}

