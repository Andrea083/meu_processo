package com.tg.meu_processo.repository;

import com.tg.meu_processo.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    boolean existsByCpf(String cpf); // verifica se existe sem buscar o objeto inteiro
    boolean existsByEmail(String email);

}


/* Optional
A busca por e-mail pode não encontrar ninguém.
Em vez de retornar null (que poderia esquecer de checar e estourar erro),
retorna um Optional que  obriga a tratar o caso "não existe".
 */