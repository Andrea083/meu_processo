# Propósito
Meu Processo é um sistema web de acompanhamento de processos jurídicos que traduz automaticamente jargão legal para linguagem acessível ao leigo. 
Permite que clientes, advogados e administradores gerenciem processos, acessem movimentações e recebam notificações de andamentos processuais traduzidos.

# O Que Está Dentro
✅ API REST com Spring Boot 3.2 + Java 17
✅ Autenticação JWT com controle de acesso por perfil (RBAC)
✅ Tradutor NLP via Gemini para termos jurídicos
✅ Pipeline automatizado: coleta → tradução → persistência
✅ PostgreSQL como banco de dados relacional
✅ Docker Compose para ambiente local

# Requisitos
JDK: Java 17+
Build: Maven 3.8+
Database: PostgreSQL 15+
Docker: Docker Compose V2 (recomendado)

# Estrutura do Projeto
meu_processo/
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── tg/
        │           └── meu_processo/
        │               ├── config/
        │               │   └── SpringDocConfig
        │               ├── controller/
        │               │   ├── AuthController
        │               │   ├── IaTesteController
        │               │   ├── MovimentacaoController
        │               │   ├── ProcessoController
        │               │   └── UsuarioController
        │               ├── dto/
        │               │   ├── LoginDTO
        │               │   ├── MovimentacaoCreateDTO
        │               │   ├── MovimentacaoDTO
        │               │   ├── ProcessoCreateDTO
        │               │   ├── ProcessoDTO
        │               │   ├── RecuperarSenhaDTO
        │               │   ├── SenhaUpdateDTO
        │               │   ├── UsuarioCreateDTO
        │               │   └── UsuarioDTO
        │               ├── entity/
        │               │   ├── enums/
        │               │   │   ├── PerfilUsuario
        │               │   │   └── StatusProcesso
        │               │   ├── Movimentacao
        │               │   ├── Processo
        │               │   └── Usuario
        │               ├── repository/
        │               │   ├── MovimentacaoRepository
        │               │   ├── ProcessoRepository
        │               │   └── UsuarioRepository
        │               ├── security/
        │               │   ├── AuthenticatedUserService
        │               │   ├── JwtAuthenticationFilter
        │               │   ├── JwtService
        │               │   ├── SecurityConfig
        │               │   └── UserDetailsServiceImpl
        │               ├── service/
        │               │   ├── DicionarioJuridico
        │               │   ├── MovimentacaoService
        │               │   ├── NlpTradutorService
        │               │   ├── ProcessoService
        │               │   ├── ScraperSimulatorService
        │               │   └── UsuarioService
        │               ├── DataInitializer
        │               └── MeuProcessoApplication
        └── resources/
            ├── static/
            ├── templates/
            └── application.properties


# Endpoints Principais
Autenticação:
POST /api/auth/login

Usuários:
GET    /api/usuarios
POST   /api/usuarios 
PUT    /api/usuarios/{id}
DELETE /api/usuarios/{id}

Processos:
GET    /api/processos
POST   /api/processos      
PUT    /api/processos/{id}
DELETE /api/processos/{id} 

POST /ia/traduzir 
JSON { "texto": " " }

# Funcionalidades Principais
1. Autenticação JWT
Segurança stateless com tokens JWT. Suporta 3 perfis: ADMINISTRADOR, ADVOGADO, CLIENTE.

2. Tradução por dicionário estático e chamada à IA caso o termo não seja encontrado no dicionário
Exemplo de conversão de termos técnicos:
"Deferida tutela de urgência" → "O juiz concordou em proteger seus direitos imediatamente"
"Embargos de Declaração rejeitados" → "Seu pedido foi negado"

3. Pipeline Automatizado
Coleta → NLP Tradutor → Persistência PostgreSQL → Notificação

4. Controle de Acesso Granular
Admin: CRUD completo
Advogado: Gerencia seus processos
Cliente: Visualiza apenas seus processos
