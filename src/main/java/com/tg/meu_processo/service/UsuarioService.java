package com.tg.meu_processo.service;

import com.tg.meu_processo.dto.*;
import com.tg.meu_processo.entity.Usuario;
import com.tg.meu_processo.entity.enums.PerfilUsuario;
import com.tg.meu_processo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public boolean existePorId(Long id) {
        return repository.existsById(id);
    }

    public List<UsuarioDTO> listarTodos() {
        return repository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public UsuarioDTO buscarPorId(Long id) {
        return repository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    public Map<String, Object> criarPorAdmin(UsuarioCreateDTO dto) {
        if (repository.existsByEmail(dto.email())) {
            throw new RuntimeException("Email já cadastrado");
        }
        if (repository.existsByCpf(dto.cpf())) {
            throw new RuntimeException("CPF já cadastrado");
        }

        String senhaGerada = gerarSenha4Digitos();

        Usuario usuario = Usuario.builder()
                .nome(dto.nome())
                .cpf(dto.cpf())
                .cargo(dto.perfil() == PerfilUsuario.ADMINISTRADOR ? dto.cargo() : null)
                .oab(dto.perfil() == PerfilUsuario.ADVOGADO ? dto.oab() : null)
                .email(dto.email())
                .whatsapp(dto.whatsapp())
                .senha(passwordEncoder.encode(senhaGerada))
                .perfil(dto.perfil())
                .build();

        Usuario saved = repository.save(usuario);

        return Map.of(
                "id", saved.getId(),
                "email", saved.getEmail(),
                "senhaTemporaria", senhaGerada,
                "mensagem", "Usuário criado. Envie a senha ao usuário."
        );
    }

    public UsuarioDTO atualizar(Long id, UsuarioCreateDTO dto) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        usuario.setNome(dto.nome());
        usuario.setCpf(dto.cpf());
        usuario.setEmail(dto.email());
        usuario.setWhatsapp(dto.whatsapp());

        if (usuario.getPerfil() == PerfilUsuario.ADMINISTRADOR) {
            usuario.setCargo(dto.cargo());
        }
        if (usuario.getPerfil() == PerfilUsuario.ADVOGADO) {
            usuario.setOab(dto.oab());
        }

        return toDTO(repository.save(usuario));
    }

    public void alterarSenha(Long userId, SenhaUpdateDTO dto) {
        Usuario usuario = repository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!passwordEncoder.matches(dto.senhaAtual(), usuario.getSenha())) {
            throw new RuntimeException("Senha atual incorreta");
        }

        usuario.setSenha(passwordEncoder.encode(dto.novaSenha()));
        repository.save(usuario);
    }

    public Map<String, String> recuperarSenha(String email) {
        Usuario usuario = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email não encontrado"));

        String novaSenha = gerarSenha4Digitos();
        usuario.setSenha(passwordEncoder.encode(novaSenha));
        repository.save(usuario);

        // TODO: Enviar por email/whatsapp
        return Map.of(
                "mensagem", "Nova senha gerada",
                "novaSenha", novaSenha // Em produção, enviar por email, não retornar!
        );
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }

    private String gerarSenha4Digitos() {
        SecureRandom random = new SecureRandom();
        int numero = random.nextInt(10000);
        return String.format("%04d", numero);
    }

    private UsuarioDTO toDTO(Usuario u) {
        return new UsuarioDTO(
                u.getId(), u.getNome(), u.getCpf(), u.getCargo(),
                u.getOab(), u.getEmail(), u.getWhatsapp(), u.getPerfil()
        );
    }
    public boolean isOwner(Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getPrincipal() == null) {
            System.out.println("Auth is null");
            return false;
        }
        Usuario usuario = (Usuario) auth.getPrincipal();

        System.out.println("ID do token: " + usuario.getId() + " | ID da URL: " + id);
        return usuario.getId().equals(id);
    }
}

