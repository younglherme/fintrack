# FinTrack API 💰

FinTrack é uma API REST para gerenciamento de finanças pessoais. Desenvolvida com **Spring Boot**, **Kotlin** e **PostgreSQL**, oferece funcionalidades completas para controlar suas despesas e receitas.

## 📋 Características

- ✅ **Autenticação e Autorização** com JWT
- ✅ **Gerenciamento de Usuários** com criptografia de senha
- ✅ **Gerenciamento de Categorias** de transações
- ✅ **Gerenciamento de Transações** (receitas e despesas)
- ✅ **Validação de Dados** robusta
- ✅ **Cobertura de Testes** com JaCoCo (80%+)
- ✅ **Documentação API** com Swagger/OpenAPI
- ✅ **Health Checks** e Actuator endpoints
- ✅ **Migrations** automáticas com Flyway
- ✅ **Docker Support** com docker-compose

## 🛠️ Stack Tecnológico

| Tecnologia | Versão |
|-----------|--------|
| **Java** | 17 |
| **Kotlin** | 2.2.21 |
| **Spring Boot** | 3.5.14 |
| **PostgreSQL** | 16 |
| **JWT (JJWT)** | 0.13.0 |
| **JUnit 5** | Latest |
| **Mockito** | 6.1.0 |

## 🚀 Como Iniciar

### Pré-requisitos

- Java 17+
- PostgreSQL 16 (ou Docker)
- Gradle 8.x

### Instalação Local

1. **Clone o repositório**
   ```bash
   git clone <seu-repositorio>
   cd fintrack
   ```

2. **Inicie o PostgreSQL com Docker** (opcional)
   ```bash
   docker-compose up -d
   ```

3. **Configure as variáveis de ambiente** (opcional)
   ```bash
   export SPRING_PROFILES_ACTIVE=dev
   export JWT_SECRET=sua-chave-secreta
   export JWT_EXPIRATION_IN_MS=86400000
   ```

4. **Execute a aplicação**
   ```bash
   ./gradlew bootRun
   ```

   Ou no Windows:
   ```bash
   gradlew.bat bootRun
   ```

A API estará disponível em `http://localhost:8080`

## 🔐 Autenticação

A API utiliza **JWT (JSON Web Tokens)** para autenticação. Após fazer login ou registrar-se, você receberá um token que deve ser incluído nas requisições subsequentes no header:

```
Authorization: Bearer <seu-token-jwt>
```

### Endpoints de Autenticação

- `POST /auth/register` - Registrar novo usuário
- `POST /auth/login` - Fazer login

## 📚 Endpoints Principais

### Usuários
- `GET /users/{id}` - Obter dados do usuário
- `PUT /users/{id}` - Atualizar usuário

### Categorias
- `GET /categories` - Listar todas as categorias
- `POST /categories` - Criar nova categoria
- `PUT /categories/{id}` - Atualizar categoria
- `DELETE /categories/{id}` - Deletar categoria

### Transações
- `GET /transactions` - Listar transações
- `POST /transactions` - Criar transação
- `PUT /transactions/{id}` - Atualizar transação
- `DELETE /transactions/{id}` - Deletar transação

## 🧪 Testes

### Executar todos os testes
```bash
./gradlew test
```

### Gerar relatório de cobertura de testes
```bash
./gradlew jacocoTestReport
```

O relatório HTML estará disponível em `build/reports/jacoco/test/html/index.html`

### Verificar cobertura mínima (80%)
```bash
./gradlew jacocoTestCoverageVerification
```

## 📖 Documentação API

Swagger UI disponível em: `http://localhost:8080/swagger-ui.html`

JSON da API: `http://localhost:8080/v3/api-docs`

## 🏗️ Arquitetura

O projeto segue uma arquitetura em camadas:

```
src/main/kotlin/dev/guilhermesilva/fintrack/
├── application/      # Serviços e DTOs (lógica de negócio)
├── auth/            # Autenticação e autorização
├── domain/          # Entidades e repositórios (domínio)
├── infra/           # Infraestrutura (segurança, exceções, etc)
└── FintrackApplication.kt  # Classe principal
```

## 📝 Variáveis de Ambiente

| Variável | Padrão | Descrição |
|----------|--------|-----------|
| `SPRING_PROFILES_ACTIVE` | `dev` | Perfil ativo (dev, prod, test) |
| `SERVER_PORT` | `8080` | Porta do servidor |
| `JWT_SECRET` | - | Chave secreta para JWT (deve ser alterada em produção) |
| `JWT_EXPIRATION_IN_MS` | `86400000` | Tempo de expiração do JWT em ms (1 dia) |
| `SPRING_DATASOURCE_URL` | - | URL do banco de dados PostgreSQL |
| `SPRING_DATASOURCE_USERNAME` | - | Usuário do banco de dados |
| `SPRING_DATASOURCE_PASSWORD` | - | Senha do banco de dados |

## 🐳 Docker

### Iniciar com Docker Compose

```bash
docker-compose up -d
```

Isso iniciará um container PostgreSQL com as configurações corretas.

### Parar os containers

```bash
docker-compose down
```

## 🔍 Health Check

A aplicação expõe um endpoint de health check:

```
GET /actuator/health
```

Resposta esperada:
```json
{
  "status": "UP"
}
```

## 📦 Build para Produção

Gerar arquivo JAR:
```bash
./gradlew build
```

O arquivo JAR estará em: `build/libs/fintrack-api-0.0.1-SNAPSHOT.jar`

Executar:
```bash
java -jar build/libs/fintrack-api-0.0.1-SNAPSHOT.jar
```

## 🐛 Troubleshooting

### Erro de conexão com PostgreSQL
Verifique se o PostgreSQL está rodando e acessível. Use Docker Compose se não tiver PostgreSQL instalado:
```bash
docker-compose up -d postgres
```

### Erro de porta em uso
Se a porta 8080 estiver em uso, altere via variável de ambiente:
```bash
export SERVER_PORT=9090
```

### Token JWT inválido
Certifique-se que está enviando o token corretamente no header:
```
Authorization: Bearer <token>
```

## 📞 Suporte

Para dúvidas ou problemas, abra uma issue no repositório.

## 📄 Licença

Este projeto está sob licença MIT.

---

**Desenvolvido com ❤️ por Guilherme Silva**

## 👔 Descrição para Currículo

### Descrição Executiva
**FinTrack API** - Sistema completo de gerenciamento de finanças pessoais desenvolvido com Spring Boot 3, Kotlin e PostgreSQL. API REST robusta com autenticação JWT, camadas bem definidas, testes automatizados (80%+ cobertura com JaCoCo), documentação Swagger e deployment com Docker.

### Descrição Técnica Detalhada

Desenvolvedor e líder técnico de um projeto full-stack de gerenciamento financeiro pessoal. O projeto demonstra profundo conhecimento em arquitetura de software, desenvolvimento backend moderno e boas práticas de engenharia.

**Destaques Técnicos:**
- **Arquitetura em Camadas**: Implementação de padrão de arquitetura profissional com separação clara entre camadas de apresentação (Controllers), aplicação (Services), domínio (Domain) e infraestrutura (Infra)
- **Autenticação Segura**: Sistema de autenticação com JWT (JJWT), criptografia BCrypt e controle de acesso baseado em papéis (RBAC)
- **Validação Robusta**: Validação em cascata utilizando Jakarta Validation com mensagens de erro customizadas
- **Tratamento de Exceções**: Implementação de tratamento centralizado de exceções com exceções customizadas por domínio (BusinessException)
- **Segurança em Camadas**: Configuração Spring Security estateless com JWT AuthenticationFilter personalizado
- **Gerenciamento de Transações**: Uso correto de @Transactional e gerenciamento de ciclo de vida de entidades JPA
- **Testes Automatizados**: Suite completa com Mockito, MockMvc e JUnit 5, com verificação de cobertura mínima de 80%
- **CI/CD Ready**: Relatórios JaCoCo integrados, build automatizado com Gradle
- **Documentação API**: OpenAPI 3.0 com Swagger UI integrado
- **Migrations de Banco**: Versionamento de schema com Flyway
- **Docker & DevOps**: Docker Compose para ambiente de desenvolvimento e produção

**Funcionalidades Implementadas:**
- Cadastro e login de usuários com validação de segurança
- CRUD completo de categorias financeiras com isolamento por usuário
- CRUD de transações com suporte a receitas e despesas
- Relatórios de saúde da aplicação com Spring Boot Actuator
- Endpoints protegidos por autenticação e autorização
- Validação de negócio e integridade de dados

**Tecnologias & Ferramentas:**
- **Linguagem**: Kotlin (2.2.21) com funcionalidades modernas como extension functions
- **Framework**: Spring Boot 3.5.14, Spring Security, Spring Data JPA
- **Database**: PostgreSQL 16 com suporte a migrations automáticas
- **Testing**: JUnit 5, Mockito, JaCoCo (cobertura 80%+)
- **Build**: Gradle 8.x com configuração otimizada
- **API**: OpenAPI 3.0, Swagger UI
- **DevOps**: Docker, Docker Compose

**Diferenciais:**
- ✅ Código limpo seguindo SOLID principles
- ✅ Padrões de design (Repository, Service, DTO, Factory)
- ✅ Cobertura de testes acima de 80%
- ✅ Tratamento robusto de erros de negócio
- ✅ Health checks e monitoramento
- ✅ Pronto para produção com configurações de ambiente
- ✅ Documentação completa com exemplos
