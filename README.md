# FinTrack API

![CI](https://github.com/younglherme/fintrack/actions/workflows/ci.yml/badge.svg)
![Docker Build](https://github.com/younglherme/fintrack/actions/workflows/docker-build.yml/badge.svg)
![Java](https://img.shields.io/badge/Java-17-orange)
![Kotlin](https://img.shields.io/badge/Kotlin-2.2-blueviolet)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-brightgreen)
![Coverage](https://img.shields.io/badge/Coverage-80%25%2B-success)

API REST para gerenciamento de finanças pessoais, desenvolvida em Kotlin com Spring Boot.

## Sobre o projeto

O FinTrack API permite que usuários gerenciem receitas e despesas pessoais com autenticação JWT, categorias financeiras e filtros por período, tipo e categoria.

Este projeto foi desenvolvido como portfólio backend, com foco em boas práticas de mercado, testes automatizados, Docker, CI/CD e documentação OpenAPI.

## Funcionalidades

* Cadastro de usuário
* Login com JWT
* Autenticação stateless
* CRUD de categorias
* CRUD de transações
* Filtros por:

    * Tipo: `INCOME` ou `EXPENSE`
    * Categoria
    * Período
* Paginação
* Validações com Bean Validation
* Tratamento global de erros
* Swagger/OpenAPI
* Testes automatizados
* Cobertura mínima com JaCoCo
* Docker e Docker Compose
* GitHub Actions CI

## Tecnologias

* Kotlin
* Java 17
* Spring Boot 3
* Spring Security
* JWT com JJWT
* Spring Data JPA
* PostgreSQL
* Flyway
* Docker
* Docker Compose
* JUnit 5
* Mockito-Kotlin
* MockMvc
* JaCoCo
* Swagger/OpenAPI 3
* GitHub Actions

## Arquitetura de pacotes

```text
src/main/kotlin/dev/guilhermesilva/fintrack
├── application
│   ├── auth
│   ├── category
│   ├── common
│   └── transaction
├── domain
│   ├── category
│   ├── transaction
│   └── user
└── infra
    ├── config
    ├── exception
    └── security
```

## Pré-requisitos

Para rodar o projeto localmente, você precisa ter instalado:

* Java 17
* Docker Desktop
* Git

## Como rodar localmente

Clone o repositório:

```bash
git clone https://github.com/younglherme
cd fintrack
```

Suba o PostgreSQL com Docker:

```bash
docker compose up -d postgres
```

No Windows PowerShell:

```powershell
docker compose up -d postgres
```

Execute a aplicação:

```bash
./gradlew bootRun
```

No Windows PowerShell:

```powershell
.\gradlew bootRun
```

A API ficará disponível em:

```text
http://localhost:8080
```

## Como rodar com Docker Compose

Crie um arquivo `.env` na raiz do projeto com base no `.env.example`.

Depois rode:

```bash
docker compose up -d --build
```

No Windows PowerShell:

```powershell
docker compose up -d --build
```

A API ficará disponível em:

```text
http://localhost:8080
```

O PostgreSQL ficará disponível em:

```text
localhost:5433
```

## Swagger

A documentação da API pode ser acessada em:

```text
http://localhost:8080/swagger-ui.html
```

Para testar rotas protegidas:

1. Execute `POST /auth/register` ou `POST /auth/login`
2. Copie o token retornado
3. Clique em `Authorize`
4. Cole o token no formato:

```text
Bearer SEU_TOKEN
```

## Endpoints principais

### Auth

| Método | Endpoint         | Descrição                             |
| ------ | ---------------- | ------------------------------------- |
| POST   | `/auth/register` | Registra um novo usuário              |
| POST   | `/auth/login`    | Autentica usuário e retorna token JWT |

### Categories

| Método | Endpoint           | Descrição                               |
| ------ | ------------------ | --------------------------------------- |
| POST   | `/categories`      | Cria uma categoria                      |
| GET    | `/categories`      | Lista categorias do usuário autenticado |
| GET    | `/categories/{id}` | Busca categoria por ID                  |

### Transactions

| Método | Endpoint             | Descrição                                |
| ------ | -------------------- | ---------------------------------------- |
| POST   | `/transactions`      | Cria uma transação                       |
| GET    | `/transactions`      | Lista transações com filtros e paginação |
| GET    | `/transactions/{id}` | Busca transação por ID                   |
| PUT    | `/transactions/{id}` | Atualiza uma transação                   |
| DELETE | `/transactions/{id}` | Remove uma transação                     |

## Exemplos de requisições

### Registrar usuário

```json
{
  "name": "Guilherme Silva",
  "email": "gui@gmail.com",
  "password": "12345678"
}
```

### Login

```json
{
  "email": "gui@gmail.com",
  "password": "12345678"
}
```

### Criar categoria

```json
{
  "name": "Alimentação",
  "type": "EXPENSE"
}
```

### Criar transação

```json
{
  "amount": 59.90,
  "description": "Mercado",
  "type": "EXPENSE",
  "transactionDate": "2026-05-26",
  "categoryId": "UUID_DA_CATEGORIA"
}
```

## Filtros de transações

Exemplos:

```text
GET /transactions?type=EXPENSE
GET /transactions?startDate=2026-05-01&endDate=2026-05-31
GET /transactions?categoryId=UUID_DA_CATEGORIA
GET /transactions?page=0&size=10&sort=transactionDate,desc
```

## Tratamento de erros

A API utiliza um `GlobalExceptionHandler` para padronizar respostas de erro.

Exemplo:

```json
{
  "timestamp": "2026-05-26T13:21:06.259Z",
  "status": 404,
  "error": "Not Found",
  "message": "Transaction not found",
  "path": "/transactions/..."
}
```

## Rodar testes

```bash
./gradlew test
```

No Windows PowerShell:

```powershell
.\gradlew test
```

## Gerar relatório de cobertura

```bash
./gradlew clean test jacocoTestReport
```

No Windows PowerShell:

```powershell
.\gradlew clean test jacocoTestReport
```

Relatório HTML:

```text
build/reports/jacoco/test/html/index.html
```

## Validar cobertura mínima

```bash
./gradlew jacocoTestCoverageVerification
```

No Windows PowerShell:

```powershell
.\gradlew jacocoTestCoverageVerification
```

A cobertura mínima configurada é de 80%.

## Variáveis de ambiente

| Variável                       | Descrição                   | Exemplo             |
| ------------------------------ | --------------------------- | ------------------- |
| `APP_PORT`                     | Porta da aplicação          | `8080`              |
| `DB_HOST`                      | Host do banco               | `localhost`         |
| `DB_PORT`                      | Porta do banco              | `5433`              |
| `DB_NAME`                      | Nome do banco               | `fintrack`          |
| `DB_USERNAME`                  | Usuário do banco            | `fintrack_user`     |
| `DB_PASSWORD`                  | Senha do banco              | `fintrack_password` |
| `JWT_SECRET`                   | Segredo JWT em Base64       | `...`               |
| `JWT_EXPIRATION_IN_MS`         | Tempo de expiração do token | `86400000`          |
| `SPRINGDOC_SWAGGER_UI_ENABLED` | Habilita Swagger UI         | `true`              |
| `SPRINGDOC_API_DOCS_ENABLED`   | Habilita OpenAPI docs       | `true`              |

## Comandos úteis

Subir containers:

```bash
docker compose up -d
```

Subir containers reconstruindo a imagem:

```bash
docker compose up -d --build
```

Ver logs da aplicação:

```bash
docker compose logs -f app
```

Ver logs do PostgreSQL:

```bash
docker compose logs -f postgres
```

Parar containers:

```bash
docker compose down
```

Evite usar em ambiente de desenvolvimento com dados importantes:

```bash
docker compose down -v
```

Esse comando remove o volume do PostgreSQL e apaga os dados locais.

## CI/CD

O projeto utiliza GitHub Actions para:

* Compilar o projeto
* Rodar testes automatizados
* Validar cobertura mínima com JaCoCo
* Gerar relatório de cobertura
* Validar build da imagem Docker

Workflows:

```text
.github/workflows/ci.yml
.github/workflows/docker-build.yml
```
