package com.tg.meu_processo.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity      // ativa a segurança web do Spring Security
@EnableMethodSecurity   // permite segurança por método (@PreAuthorize, @Secured, etc.)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter; //filtro JWT, injetado pelo construtor do Lombok

    //Cadeia de filtros de segurança
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                //Habilita o CORS usando as regras definidas no método corsConfigurationSource()
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                //Desabilita a proteção CSRF. Correto para APIs stateless com JWT (não usa cookies de sessão)
                .csrf(csrf -> csrf.disable())

                //Não cria sessão HTTP no servidor. Cada requisição se autentica sozinha via token — essência do JWT.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                //Início das regras de autorização por rota.
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/usuarios/recuperar-senha").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().authenticated()
                )
                //Insere o filtro JWT antes do filtro padrão de login. Assim o token é validado e o usuário é autenticado antes do Spring tentar o fluxo tradicional de usuário/senha.
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build(); // constrói e retorna a cadeia configurada
    }

    //Configuração do CORS
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration(); // objeto que guarda as regras de CORS
        //Permite requisições apenas desses dois front-ends (portas típicas de Vite e React).
        config.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://localhost:3000"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("*")); //Aceita qualquer cabeçalho na requisição
        config.setAllowCredentials(true); //Permite envio de credenciais (cookies/headers de auth)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // aplica essa config a todas as rotas
        return source;
    }

    //Codificador de senha
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // hash BCrypt para armazenar/verificar senhas
    }

    //Gerenciador de autenticação
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager(); // expõe o AuthenticationManager do Spring como bean
    }
}

/*
 Requisição chega → passa pelo CORS.
 jwtAuthFilter valida o token e autentica o usuário.
 Regras de authorizeHttpRequests decidem se a rota é pública ou exige login.
 Sem sessão no servidor: tudo depende do token a cada requisição.
 */

/*
CORS (Cross-Origin Resource Sharing) é um mecanismo de segurança do navegador que controla se
um site pode fazer requisições para um servidor de outra origem:
Front-end em localhost:3000 chama a API em localhost:5173.
O navegador envia (para POST/PUT/DELETE) uma requisição preflight OPTIONS perguntando: "posso?"
O servidor responde com headers do tipo Access-Control-Allow-Origin.
Se a origem estiver autorizada, o navegador libera a requisição real. Senão, bloqueia.
 */