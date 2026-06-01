# Project Context

## Overview

Personal Finances Tracker — a micro-frontend application for managing personal finances month by month, with quarter-based budgeting and expense tracking.

## Strategy

- First of multiple micro-frontend applications composed under a micro-lc launcher
- Open source on GitHub (https://github.com/jlmonteiro/personal-finances-tracker)
- Learning project for Kotlin + JOOQ + Spring Boot 4.x

## Technology Stack

| Layer | Technology |
|-------|-----------|
| Backend | Kotlin, Spring Boot 4.x, JDK 21 |
| Security | Spring Security OAuth2 Resource Server (dev: fake identity) |
| Build | Gradle 8.14 (Kotlin DSL), version catalog |
| Database | PostgreSQL 17 (Docker Compose), Liquibase, JOOQ |
| Mapping | MapStruct (Kotlin data classes) |
| Frontend | React 19, Mantine 7, Vite, TypeScript |
| Icons | Tabler Icons (via Mantine) |
| State | TanStack Query (React Query) |
| Forms | React Hook Form + Zod |
| Testing (BE) | JUnit 5 + Cucumber (Kotlin step defs), Testcontainers |
| Testing (FE) | Vitest + Testing Library + MSW (integration), Playwright + Cucumber (E2E) |
| CI/CD | GitHub Actions |
| IDs | UUIDv7 (application-generated, DB default fallback) |

## Architecture

- **Multi-module Gradle build:** `backend/` (Spring Boot API) + `frontend/` (React SPA)
- **No bundled JAR:** Frontend and backend deploy independently
- **Dev mode:** Vite proxy `/api` → `localhost:8080`, fake auth identity
- **Production:** Frontend served by nginx/micro-lc, backend validates Google OAuth2 JWT
- **Micro-frontend:** No top-level chrome, renders inside micro-lc content area

## Key Conventions

- API First (OpenAPI 3.x), REST under `/api/v1/`
- RFC 7807 error responses
- Monetary values as strings (no floats)
- JOOQ records never leave the service layer → MapStruct → Kotlin data class DTOs
- Expense status derived (not stored): Pending, Paid, Overdue
- Pagination on all list endpoints (`?page=1&size=20`)
- Expand-contract pattern for schema migrations

## Specifications

All specs live in `.specs/`:
- `requirements/` — Functional requirements (EARS pattern)
- `design/` — ADRs, data models, REST architecture, frontend architecture, test scenarios
- `tasks/` — Epic/story breakdown

## Wireframes

UI wireframes in `docs/wireframes/` using Markdown-UI DSL (`.ui.md` files).

## Running Locally

```bash
# Backend (auto-starts PostgreSQL via Docker Compose)
./gradlew :backend:bootRun

# Frontend
cd frontend && npm run dev
```

## Links

- [Requirements Index](.specs/requirements/requirements.md)
- [Design Index](.specs/design/design.md)
- [Tasks Index](.specs/tasks/tasks.md)
- [ADRs](.specs/design/adrs.md)
