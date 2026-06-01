# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## 0.0.1

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
