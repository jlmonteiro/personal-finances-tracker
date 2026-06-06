# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [1.0.0] - 2026-06-06

### Added

- Helm chart for Kubernetes deployment (backend + frontend)
- Dockerfiles for backend (JDK 21 multi-stage) and frontend (nginx multi-stage)
- GitHub Actions workflow to publish Docker images and Helm chart to ghcr.io
- Local deploy script (`scripts/deploy-local.sh`) for k3s
- Spring Boot Actuator with health/readiness/liveness probes
- Configurable database schema via `SPRING_DATASOURCE_DEFAULT_SCHEMA` env var
- Single ingress with path-based routing (`/api` → backend, `/` → frontend)

### Changed

- Bumped version to 1.0.0 (first production release)
- Frontend error handling: retry with backoff + error state instead of infinite loading
- Version consistency script now checks Chart.yaml
- Pre-commit `check-yaml` excludes Helm templates

## [0.4.0] - 2026-06-02

### Added

- Categories API (CRUD with icon, duplicate name detection, 17 Cucumber tests)
- Payees API (CRUD with category associations, validation, 16 Cucumber tests)
- Categories frontend page (table with rendered Tabler icons, modal forms, delete confirmation)
- Payees frontend page (MultiSelect for category associations, modal forms)
- Sidebar navigation entries for Categories and Payees
- Liquibase migration for categories, payees, payee_categories tables

## [0.3.0] - 2026-06-02

### Added

- Setup wizard (3 steps: currency, month start day, income sources)
- Navigation guard (redirect to /setup if not configured)
- Settings page (edit configuration + manage income sources)
- Sidebar navigation (Dashboard, Settings)
- IncomeSourceForm shared component with structured date rules
- API client modules (configuration, income-sources)
- useConfiguration hook (TanStack Query)
- React Router, QueryClientProvider, Notifications providers
- Improved error notifications showing actual API error messages

## [0.2.0] - 2026-06-02

### Added

- Income Sources CRUD API (GET list, POST, PATCH, DELETE)
- Income sources database migration
- Frequency and PaymentDateType enums
- Money DTO ({value, currency}) per API guidelines
- Paginated response wrapper
- ScenarioContext for Cucumber test isolation
- ValidationException in exception hierarchy
- 18 Cucumber scenarios for income sources

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
