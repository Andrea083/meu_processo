package com.tg.meu_processo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component // registra a classe como bean gerenciado pelo Spring
@RequiredArgsConstructor // Lombok: gera construtor com os campos final

public class JwtAuthenticationFilter extends OncePerRequestFilter { //garante que o filtro roda uma vez por requisição
    //injetados via construtor
    private final JwtService jwtService; // lógica de token (extrair, validar)
    private final UserDetailsServiceImpl userDetailsService; // busca o usuário no banco

    @Override //sobrescreve porque o método já existe em OncePerRequestFilter
    protected boolean shouldNotFilter(HttpServletRequest request) { //quando pular o filtro
        String path = request.getServletPath(); // pega o caminho da URL
        return path.startsWith("/api/auth/"); // ignora rotas de login/registro
        // para conseguir logar sem token
    }

    @Override
    //método protegido, que não retorna nada, recebe uma requisição, uma resposta e a cadeia de filtros,
    // e que pode lançar as exceções ServletException e IOException
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization"); // lê o header Authorization
        System.out.println("=== JWT FILTER ===");
        System.out.println("Auth Header: " + authHeader); // debug (remover em produção)

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("Token ausente ou inválido");
            filterChain.doFilter(request, response); // deixa a requisição seguir sem autenticar
            return;                                 // encerra o filtro aqui
        }

        String token = authHeader.substring(7); // remove "Bearer " (7 caracteres)
        String email = jwtService.extractEmail(token);    // extrai o email de dentro do token
        System.out.println("Email extraído: " + email);

        //autentica se extraiu um email E ainda não há ninguém autenticado no contexto (evita retrabalho)
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(email); // carrega usuário do banco

            if (jwtService.isTokenValid(token)) { // valida assinatura/expiração do token
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
            //Cria o objeto de autenticação do Spring Security.
            //null nas credenciais porque a senha não é usada (o token já provou a identidade).
            //Guarda no SecurityContext → a partir daqui o usuário está logado nesta requisição.
        }

        filterChain.doFilter(request, response); // passa para o próximo filtro/controller
    }
}

//o 'throws' apenas declara que o erro pode acontecer e repassa a responsabilidade de tratá-lo para quem chamou o método