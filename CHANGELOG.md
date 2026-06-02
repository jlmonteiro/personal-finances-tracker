# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [0.1.0] - 2026-06-02

### Added

- Configuration API (GET/POST/PATCH /api/v1/configuration)
- Bean Validation for request DTOs
- Exception hierarchy (BaseException → BusinessException → EntityNotFound/EntityConflict)
- Global exception handler (RFC 7807 ProblemDetail)
- MapStruct mapper with kapt for JOOQ record-to-DTO conversion
- JOOQ DefaultConfigurationCustomizer (lowercase name rendering)
- UUIDv7 utility (uuid-creator library)
- Spring Boot Liquibase starter for auto-configuration
- 15 Cucumber integration tests (configuration API)
- Version consistency pre-commit hook
- ADR-10 (kapt for annotation processing)
- ADR-11 (UUIDv7 for primary keys)
- EPIC-2 task breakdown (Setup & Configuration)

### Changed

- Bumped version to 0.1.0
- Switched from liquibase-core to spring-boot-starter-liquibase

## [0.0.1] - 2026-06-01

### Added

- Gradle multi-module project scaffold (backend + frontend)
- Spring Boot 4.x backend with Kotlin
- React + Mantine + Vite frontend with TypeScript
- Docker Compose with PostgreSQL 17
- Liquibase + JOOQ code generation pipeline (LiquibaseDatabase, no Docker needed in build)
- Initial migration: `app_configuration` table
- Pre-commit hooks (secrets detection, conventional commits, ESLint)
- GitHub Actions CI workflows (backend + frontend, path-filtered)
- SDD specifications (requirements, design, tasks)
- UI wireframes (Markdown-UI DSL)
- Project context and coding guidelines
