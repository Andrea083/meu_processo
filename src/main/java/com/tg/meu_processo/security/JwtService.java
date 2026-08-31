package com.tg.meu_processo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service // registra como bean de serviço no Spring
public class JwtService {

    @Value("${jwt.secret}") // injeta o valor de "jwt.secret" do application.properties/yml
    private String secret; // chave secreta usada para assinar o token

    @Value("${jwt.expiration}") // injeta o tempo de expiração configurado
    private Long expiration; // duração do token em milissegundos

    //Transforma a string secreta em uma Key criptográfica usada tanto para assinar quanto para validar tokens
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    } //gera a chave HMAC a partir dos bytes do secret

    public String generateToken(String email, String perfil, Long userId) {
        return Jwts.builder()                   // inicia a construção do JWT
                .setSubject(email)              // "sub": identifica o dono do token (email)
                .claim("perfil", perfil)    // claim customizada: perfil/role do usuário
                .claim("userId", userId)    // claim customizada: id do usuário
                .setIssuedAt(new Date())       //data de emissão (agora)
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) //agora + expiração
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // assina com a chave, algoritmo HS256
                .compact();                    // monta e serializa em String (header.payload.assinatura)
    }

    //Extrair dados
    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()                 // inicia o parser
                .setSigningKey(getSigningKey())     // usa a mesma chave para validar a assinatura
                .build()
                .parseClaimsJws(token)              // lê e VALIDA o token (lança exceção se inválido/expirado)
                .getBody();                         // retorna o corpo (payload) com as claims
    }

    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    //Validar token
    public boolean isTokenValid(String token) {
        try {
            return extractClaims(token).getExpiration().after(new Date());
            // true se a data de expiração é DEPOIS de agora (ainda válido)
        } catch (Exception e) {
            return false;
            // token malformado/expirado/assinatura errada → inválido
        }
    }
}

//parseClaimsJws valida assinatura e expiração (lança exceção se expirado). isTokenValid acabou sendo redundante, mas
// deixei porque está sendo usado em JwtAuthenticationFilter