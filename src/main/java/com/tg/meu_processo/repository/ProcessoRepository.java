package com.tg.meu_processo.repository;

import com.tg.meu_processo.entity.Processo;
import com.tg.meu_processo.entity.enums.StatusProcesso;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface ProcessoRepository extends JpaRepository<Processo, Long> {
    List<Processo> findByClienteId(Long clienteId);
    List<Processo> findByAdvogadoId(Long advogadoId);
    List<Processo> findByStatus(StatusProcesso status);
}

//Escreve o nome do método	e Spring gera a query SQL/JPQL
//Escreve a interface	e Spring gera a implementação (proxy)

