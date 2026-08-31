package com.tg.meu_processo.security;

import com.tg.meu_processo.entity.Usuario;
import com.tg.meu_processo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service                    // registra a classe como bean de serviço do Spring
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    //implementa interface padrão do Spring Security para carregar dados de usuário.

    private final UsuarioRepository usuarioRepository; // acesso ao banco, injetado pelo construtor

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        //Busca o usuário pelo email. Se não existir, lança UsernameNotFoundException → o login falha.
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        //Converte Usuario (entidade da aplicação) em um User do Spring Security
        return new User(
                usuario.getEmail(),  //identificador
                usuario.getSenha(),  //hash guardado que vai ser comparado com PasswordEncoder (BCrypt).
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + usuario.getPerfil().name()))
        );      //lista com uma única permissão, no formato ROLE_ + o perfil.
    }
}

/*
É a ponte entre seu banco de dados e o Spring Security.
Quando o Spring precisa autenticar alguém, ele chama essa classe para
buscar o usuário e suas permissões.
 */

//O prefixo ROLE_ é a convenção do Spring; permite usar hasRole("ADMIN") ou @PreAuthorize("hasRole('ADMIN')").

/*
No login, o AuthenticationManager chama esse método para buscar o usuário e validar a senha.
No JwtAuthenticationFilter, normalmente também é usado para recarregar o usuário a partir do email extraído do token.
*/
/*Como cada usuário tem só um perfil, Collections.singletonList é adequado.
Se um dia precisar de múltiplos perfis/permissões por usuário, será preciso trocar por uma lista maior.
*/