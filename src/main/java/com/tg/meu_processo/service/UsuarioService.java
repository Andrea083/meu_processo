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

    // Dependências injetadas: acesso ao banco de usuários e codificador de senhas (hash)
    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public boolean existePorId(Long id) {
        return repository.existsById(id);
    }

    // Lista todos os usuários convertendo cada entidade em DTO
    public List<UsuarioDTO> listarTodos() {
        return repository.findAll().stream()
                .map(this::toDTO)// Usuario -> UsuarioDTO
                .toList();       // lista imutável
    }

    // Busca um usuário por id; lança exceção se não encontrar
    public UsuarioDTO buscarPorId(Long id) {
        return repository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    // Cria um usuário via ação de administrador, com senha temporária gerada
    public Map<String, Object> criarPorAdmin(UsuarioCreateDTO dto) {
        if (repository.existsByEmail(dto.email())) {  // Impede e-mail duplicado
            throw new RuntimeException("Email já cadastrado");
        }
        if (repository.existsByCpf(dto.cpf())) {  // Impede cpf duplicado
            throw new RuntimeException("CPF já cadastrado");
        }
        // Gera uma senha temporária de 4 dígitos
        String senhaGerada = gerarSenha4Digitos();

        // Monta a entidade Usuario usando o padrão builder (Lombok)
        Usuario usuario = Usuario.builder()
                .nome(dto.nome())
                .cpf(dto.cpf())
                .cargo(dto.perfil() == PerfilUsuario.ADMINISTRADOR ? dto.cargo() : null) // 'cargo' só é preenchido se o perfil for ADMINISTRADOR, senão null
                .oab(dto.perfil() == PerfilUsuario.ADVOGADO ? dto.oab() : null)   // 'oab' só é preenchido se o perfil for ADVOGADO, senão null
                .email(dto.email())
                .whatsapp(dto.whatsapp())
                .senha(passwordEncoder.encode(senhaGerada))  // Armazena a senha já com hash (nunca em texto puro)
                .perfil(dto.perfil())
                .build();

        Usuario saved = repository.save(usuario);

        // Retorna um mapa com dados básicos + a senha temporária em texto (para repasse)
        return Map.of(
                "id", saved.getId(),
                "email", saved.getEmail(),
                "senhaTemporaria", senhaGerada,
                "mensagem", "Usuário criado. Envie a senha ao usuário."
        );
    }

    // Atualiza dados de um usuário existente
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
        // Salva e retorna como DTO
        return toDTO(repository.save(usuario));
    }

    // Altera a senha exigindo a senha atual correta
    public void alterarSenha(Long userId, SenhaUpdateDTO dto) {
        Usuario usuario = repository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Compara a senha atual informada com o hash armazenado
        if (!passwordEncoder.matches(dto.senhaAtual(), usuario.getSenha())) {
            throw new RuntimeException("Senha atual incorreta");
        }

        // Grava a nova senha (com hash)
        usuario.setSenha(passwordEncoder.encode(dto.novaSenha()));
        repository.save(usuario);
    }

    // Recupera senha: gera uma nova e a salva (fluxo "esqueci minha senha")
    public Map<String, String> recuperarSenha(String email) {
        Usuario usuario = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email não encontrado"));

        // Gera nova senha e substitui a antiga (com hash)
        String novaSenha = gerarSenha4Digitos();
        usuario.setSenha(passwordEncoder.encode(novaSenha));
        repository.save(usuario);

        //Não seguro, o método retorna a nova senha diretamente na resposta HTTP
        return Map.of(
                "mensagem", "Nova senha gerada",
                "novaSenha", novaSenha
                // Em produção, implementar enviar por email/whatsapp invés de retornar
        );
    }

    // Exclui um usuário pelo id
    public void deletar(Long id) {
        repository.deleteById(id);
    }

    // Gera uma senha aleatória de 4 dígitos (0000 a 9999)
    private String gerarSenha4Digitos() {
        SecureRandom random = new SecureRandom();
        int numero = random.nextInt(10000);
        return String.format("%04d", numero);     // formata com zeros à esquerda (ex: 0042)
    }

    // Converte a entidade Usuario em UsuarioDTO (expõe só campos seguros, sem a senha)
    private UsuarioDTO toDTO(Usuario u) {
        return new UsuarioDTO(
                u.getId(), u.getNome(), u.getCpf(), u.getCargo(),
                u.getOab(), u.getEmail(), u.getWhatsapp(), u.getPerfil()
        );
    }
    // Verifica se o usuário autenticado é o "dono" do id informado (autorização)
    public boolean isOwner(Long id) {
        // Obtém a autenticação atual do contexto de segurança do Spring
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Se não há autenticação/principal, não é dono
        if (auth == null || auth.getPrincipal() == null) {
            System.out.println("Auth is null");
            return false;
        }
        // Recupera o usuário logado a partir do principal
        Usuario usuario = (Usuario) auth.getPrincipal();

        // Log de depuração comparando id do token com id da URL
        System.out.println("ID do token: " + usuario.getId() + " | ID da URL: " + id);
        // Retorna true se o id logado for igual ao id solicitado
        return usuario.getId().equals(id);
    }
}

//serviço de gestão de usuários — cadastro, atualização, senhas e verificação de dono.