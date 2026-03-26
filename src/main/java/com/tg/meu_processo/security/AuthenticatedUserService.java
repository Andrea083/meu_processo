//serviço de contexto do usuário
package com.tg.meu_processo.security;

import com.tg.meu_processo.entity.Usuario;
import com.tg.meu_processo.entity.enums.PerfilUsuario;
import com.tg.meu_processo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticatedUserService {

    private final UsuarioRepository usuarioRepository;

    public Usuario getUsuarioLogado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByEmail(email).orElse(null);
    }

    public boolean isAdmin() {
        Usuario u = getUsuarioLogado();
        return u != null && u.getPerfil() == PerfilUsuario.ADMINISTRADOR;
    }

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
        return u != null && u.getId().equals(userId);
    }

    public Long getUsuarioId() {
        Usuario u = getUsuarioLogado();
        return u != null ? u.getId() : null;
    }

}

