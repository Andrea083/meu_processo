# Propósito
Meu Processo é um sistema web de acompanhamento de processos jurídicos que traduz automaticamente jargão legal para linguagem acessível ao leigo. 
Permite que clientes, advogados e administradores gerenciem processos, acessem movimentações e recebam notificações de andamentos processuais traduzidos.

# O Que Está Dentro
✅ API REST com Spring Boot 3.2 + Java 17
✅ Autenticação JWT com controle de acesso por perfil (RBAC)
✅ Tradutor NLP para 50+ termos jurídicos
✅ Pipeline automatizado: coleta → tradução → persistência
✅ PostgreSQL como banco de dados relacional
✅ Docker Compose para ambiente local

# Requisitos
JDK: Java 17+
Build: Maven 3.8+
Database: PostgreSQL 15+
Docker: Docker Compose V2 (recomendado)

# Estrutura do Projeto
com.tg.meu_processo/
├── controller/
│   ├── AuthController
│   ├── UsuarioController
│   ├── ProcessoController
│   └── MovimentacaoController
├── service/
│   ├── UsuarioService
│   ├── ProcessoService
│   ├── MovimentacaoService
│   └── NlpTradutorService
├── entity/
│   ├── Usuario
│   ├── Processo
│   └── Movimentacao
├── repository/
├── security/
│   ├── JwtService
│   ├── SecurityConfig
│   └── JwtAuthenticationFilter
└── DataInitializer

# Endpoints Principais
Autenticação:
POST /api/auth/login

Usuários (Admin):
GET    /api/usuarios
POST   /api/usuarios
PUT    /api/usuarios/{id}
DELETE /api/usuarios/{id}

Processos:
GET    /api/processos
POST   /api/processos      (Apenas Admin) 
PUT    /api/processos/{id}
DELETE /api/processos/{id} (Apenas Admin)

# Funcionalidades Principais
1. Autenticação JWT
Segurança stateless com tokens JWT. Suporta 3 perfis: ADMINISTRADOR, ADVOGADO, CLIENTE.

2. Tradução NLP
Converte termos como:
"Deferida tutela de urgência" → "O juiz concordou em proteger seus direitos imediatamente"
"Embargos de Declaração rejeitados" → "Seu pedido foi negado"

3. Pipeline Automatizado
Coleta → NLP Tradutor → Persistência PostgreSQL → Notificação

4. Controle de Acesso Granular
Admin: CRUD completo
Advogado: Gerencia seus processos
Cliente: Visualiza apenas seus processos
