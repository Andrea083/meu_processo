//cria admin automaticamente
package com.tg.meu_processo;

import com.tg.meu_processo.entity.Usuario;
import com.tg.meu_processo.entity.enums.PerfilUsuario;
import com.tg.meu_processo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
// @RequiredArgsConstructor (Lombok): gera o construtor e injeta UsuarioRepository e PasswordEncoder
// CommandLineRunner faz o método run() executar uma vez, na inicialização da aplicação
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        //Verifica se já existe um usuário com e-mail
        if (usuarioRepository.findByEmail("admin@sistema.com").isEmpty()) {
            Usuario admin = Usuario.builder()
                    .nome("Administrador")
                    .cpf("00000000000")
                    .email("admin@sistema.com")
                    //senha admin123 criptografada via passwordEncoder.encode(...) (não salva em texto puro)
                    .senha(passwordEncoder.encode("admin123")) //lembrar de usar senha fixa apenas no desenvolvimento
                    .perfil(PerfilUsuario.ADMINISTRADOR)
                    .build();
            usuarioRepository.save(admin);
            System.out.println(">>> Admin criado: admin@sistema.com / admin123");
        }
    }
}

