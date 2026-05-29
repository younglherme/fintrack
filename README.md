# FinTrack API


![Java](https://img.shields.io/badge/Java-17-orange)
![Kotlin](https://img.shields.io/badge/Kotlin-2.2-blueviolet)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-brightgreen)
![Coverage](https://img.shields.io/badge/Coverage-80%25%2B-success)

API REST para gerenciamento de finanças pessoais, desenvolvida em Kotlin com Spring Boot.

## Sobre o projeto

O FinTrack API permite que usuários gerenciem receitas e despesas pessoais com autenticação JWT, categorias financeiras e filtros por período, tipo e categoria.

Este projeto foi desenvolvido como portfólio backend, com foco em boas práticas de mercado, testes automatizados, Docker, CI/CD e documentação OpenAPI.

## Funcionalidades

- Cadastro de usuário
- Login com JWT
- Autenticação stateless
- CRUD de categorias
- CRUD de transações
- Filtros por:
    - Tipo: `INCOME` ou `EXPENSE`
    - Categoria
    - Período
- Paginação
- Validações com Bean Validation
- Tratamento global de erros
- Swagger/OpenAPI
- Testes automatizados
- Cobertura mínima com JaCoCo
- Docker e Docker Compose
- GitHub Actions CI

## Tecnologias

- Kotlin
- Java 17
- Spring Boot 3
- Spring Security
- JWT com JJWT
- Spring Data JPA
- PostgreSQL
- Flyway
- Docker
- Docker Compose
- JUnit 5
- Mockito-Kotlin
- MockMvc
- JaCoCo
- Swagger/OpenAPI 3
- GitHub Actions

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