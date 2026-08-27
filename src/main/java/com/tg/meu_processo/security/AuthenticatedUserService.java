//serviço que descobre quem é o usuário autenticado na requisição atual (via Spring Security)
package com.tg.meu_processo.security;

import com.tg.meu_processo.entity.Usuario;
import com.tg.meu_processo.entity.enums.PerfilUsuario;
import com.tg.meu_processo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service //Marca a classe como um bean de serviço do Spring. Ela passa a ser gerenciada pelo container e pode ser injetada em outros lugares.
@RequiredArgsConstructor //Anotação do Lombok. Spring era automaticamente um construtor com todos os campos final

public class AuthenticatedUserService {
    //Dependência injetada. O repositório usado para buscar o usuário no banco
    private final UsuarioRepository usuarioRepository;

    //Pega o contexto de segurança da requisição atual, obtém o objeto de autenticação e extrai o nome do usuário logado (e-mail)
    public Usuario getUsuarioLogado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByEmail(email).orElse(null);
    }

    public boolean isAdmin() {
        Usuario u = getUsuarioLogado(); //reaproveita o código anterior
        return u != null && u.getPerfil() == PerfilUsuario.ADMINISTRADOR;
    } //Retorna true só se existe usuário logado (u != null) e se o perfil dele é ADMINISTRADOR
        //se u for null, nem tenta o getPerfil() (&&)

    public boolean isAdvogado() {
        Usuario u = getUsuarioLogado();
        return u != null && u.getPerfil() == PerfilUsuario.ADVOGADO;
    }

    public boolean isCliente() {
        Usuario u = getUsuarioLogado();
        return u != null && u.getPerfil() == PerfilUsuario.CLIENTE;
    }

    public boolean isOwner(Long userId) {
        Usuario u = getUsuarioLogado();
        return u != null && u.getId().equals(userId); //u.getId().equals(userId) compara o id do logado com o id recebido
    }

    public Long getUsuarioId() {
        Usuario u = getUsuarioLogado();
        return u != null ? u.getId() : null; // se existe, retorna o id; senão, null (operador ternário)
    }

}

