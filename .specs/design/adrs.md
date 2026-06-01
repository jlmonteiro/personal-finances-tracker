# Architecture Decision Records

---

## ADR-1: Multi-Module Gradle Build

**Status:** Accepted
**Date:** 2026-06-01
**Traces:** FR-14, FR-19, FR-20 (independent frontend), all backend FRs

### Problem

The application needs to support both local development (standalone) and production deployment as a micro-frontend within a launcher. A monolithic build couples frontend and backend lifecycles.

### Solution

Structure the project as a Gradle multi-module build:

```
personal-finances-tracker/
├── settings.gradle.kts
├── build.gradle.kts
├── gradle/libs.versions.toml
├── backend/
│   └── build.gradle.kts
├── frontend/
│   └── build.gradle.kts
└── docker-compose.yml
```

- `backend` — Spring Boot application (REST API only)
- `frontend` — React application (Vite), independently buildable and deployable

### Alternatives Considered

| Alternative | Pros | Cons |
|-------------|------|------|
| Single module with bundled JAR | Simple deployment, one artifact | Couples lifecycles, can't deploy frontend independently for micro-lc |
| Separate repositories | Full independence | Harder to coordinate versions, shared nothing |

### Rationale

Multi-module in one repo gives independent build/deploy while keeping version coordination simple. The frontend can be extracted to its own repo later if needed.

### Consequences

- Gradle version catalog shared across modules
- CI/CD must build and test modules independently
- Frontend uses Vite proxy in dev; no backend dependency at build time

---

## ADR-2: Spring Boot 4.x with Kotlin

**Status:** Accepted
**Date:** 2026-06-01
**Traces:** All backend FRs

### Problem

Need a backend framework for REST APIs, database access, security, and Docker Compose integration.

### Solution

Spring Boot 4.x with Kotlin as the primary language.

### Alternatives Considered

| Alternative | Pros | Cons |
|-------------|------|------|
| Quarkus + Kotlin | Faster startup, native compilation | Smaller ecosystem, less mature Kotlin support |
| Ktor | Kotlin-native, lightweight | No Spring ecosystem (security, docker compose plugin, etc.) |
| Spring Boot 3.x | Stable, well-known | Missing latest features; 4.x is current |

### Rationale

Spring Boot 4.x provides: Docker Compose plugin (auto-starts DB), OAuth2 Resource Server, Liquibase integration, and mature Kotlin support. The ecosystem coverage eliminates the need for custom infrastructure code.

### Consequences

- Requires JDK 21+
- Kotlin coroutines available but not required for this use case
- Spring Docker Compose plugin manages the database lifecycle in dev

---

## ADR-3: React + Mantine + Vite for Frontend

**Status:** Accepted
**Date:** 2026-06-01
**Traces:** FR-14, FR-15, FR-16, FR-19, FR-20 (dashboard, views)

### Problem

Need a frontend framework for a data-rich financial application with charts, calendars, tables, and forms.

### Solution

React with Mantine UI component library, bundled by Vite.

### Alternatives Considered

| Alternative | Pros | Cons |
|-------------|------|------|
| React + Material UI | Large community | Heavier, opinionated styling, less modern feel |
| Vue + Vuetify | Simpler reactivity model | Team expertise is React; micro-lc examples favour React |
| Angular + Angular Material | Full framework, strong typing | Heavy, slower iteration, overkill for this scope |

### Rationale

Mantine provides: rich component library (tables, forms, modals, date pickers), built-in Tabler Icons integration, excellent TypeScript support, and a clean modern design. Vite gives fast HMR and simple proxy configuration for dev.

### Consequences

- Tabler Icons come bundled (no extra dependency for ADR-8)
- Vite proxy handles `/api` → backend in development
- Mantine Charts (built on Recharts) available for FR-15, FR-16

---

## ADR-4: OAuth2 with Dev Fake Identity

**Status:** Accepted
**Date:** 2026-06-01
**Traces:** UJ-1 (setup), AS-1 (single user), AS-3 (launcher access)

### Problem

The application must integrate with Google OAuth via micro-lc in production, but local development should not require real OAuth tokens.

### Solution

- **Production:** Spring Security OAuth2 Resource Server validates Google JWT tokens passed by micro-lc.
- **Dev mode:** A `dev` Spring profile activates a fake identity filter that auto-injects a hardcoded user identity into the security context without requiring any token.

### Alternatives Considered

| Alternative | Pros | Cons |
|-------------|------|------|
| No auth at all | Simplest | Can't integrate with launcher; no user identity |
| Local Keycloak in dev | Realistic auth flow | Heavy; requires running another container for dev |
| Disable security entirely in dev | Simple | Code paths differ between dev and prod; bugs hide |

### Rationale

A fake identity filter keeps the security filter chain active in dev (same code path as prod) while eliminating the need for a real identity provider locally. The dev user is injected at the filter level, so controllers and services behave identically.

### Consequences

- `SecurityFilterChain` configured per profile
- Dev profile must never be activatable in production (guard via environment check)
- Frontend in dev mode skips token attachment (no Bearer header needed)

---

## ADR-5: PostgreSQL with Docker Compose

**Status:** Accepted
**Date:** 2026-06-01
**Traces:** AS-2 (Docker available)

### Problem

Need a relational database for structured financial data with ACID guarantees.

### Solution

PostgreSQL 17 (latest LTS) running in Docker Compose, with:
- Volume mounted to `~/.personal-finances-app/volumes/database` for persistence
- Spring Docker Compose plugin auto-starts the container when the backend starts

### Alternatives Considered

| Alternative | Pros | Cons |
|-------------|------|------|
| H2 (embedded) | Zero setup, in-process | Not production-grade, SQL dialect differences, no persistence guarantees |
| SQLite | File-based, simple | Limited concurrency, no JOOQ dialect support |
| Manual PostgreSQL install | No Docker dependency | Harder setup for contributors, version drift |

### Rationale

PostgreSQL provides full SQL support, JOOQ has excellent PostgreSQL dialect, and Docker Compose ensures reproducible environments. The Spring Docker Compose plugin eliminates manual container management.

### Consequences

- Contributors must have Docker installed (AS-2)
- Data persists across restarts via host volume
- `docker-compose.yml` at project root defines the database service

---

## ADR-6: Liquibase + JOOQ for Data Layer

**Status:** Accepted
**Date:** 2026-06-01

### Problem

Need versioned schema migrations and type-safe database access without ORM complexity.

### Solution

- **Liquibase** for schema migrations (changelog-based, supports rollback)
- **JOOQ** for type-safe SQL queries (code generation from schema)
- **MapStruct** for mapping between JOOQ records and DTOs
- JOOQ records never leave the service layer; controllers work with Kotlin data class DTOs

### Alternatives Considered

| Alternative | Pros | Cons |
|-------------|------|------|
| Flyway + JPA/Hibernate | Familiar ORM, less SQL | N+1 problems, lazy loading pitfalls, less control |
| Flyway + JOOQ | Flyway is simpler | Liquibase has better rollback support and XML/YAML changelogs |
| Exposed (Kotlin DSL) | Kotlin-native | Smaller community, less tooling, no code generation |

### Rationale

Liquibase + JOOQ gives full control over SQL with type safety. JOOQ code generation ensures compile-time verification of queries against the actual schema. MapStruct eliminates boilerplate mapping code. Keeping JOOQ records internal to the service layer maintains a clean architecture boundary.

### Consequences

- JOOQ code generation runs after Liquibase migrations (Gradle task dependency)
- DTOs are Kotlin data classes; MapStruct configured with `componentModel = "spring"` and Kotlin support via `mapstruct-processor` with `kapt` or `ksp`
- Service layer is the boundary: controllers ↔ DTOs ↔ services ↔ JOOQ records ↔ DB

---

## ADR-7: Micro-Frontend Composition via micro-lc

**Status:** Accepted
**Date:** 2026-06-01
**Traces:** FR-19, FR-20 (views), AS-3 (launcher access)

### Problem

This application is the first of many micro-frontends that will be composed under a shared launcher with unified authentication and navigation.

### Solution

Design the frontend as a composable micro-frontend for micro-lc:
- No top-level chrome (no app bar, no user profile, no app switcher)
- Renders inside a content area provided by the launcher
- Internal navigation only (dashboard, planning, settings)
- Loadable as a micro-lc parcel or iframe

### Alternatives Considered

| Alternative | Pros | Cons |
|-------------|------|------|
| Full standalone app with own chrome | Works without launcher | Duplicates chrome across apps; inconsistent UX |
| Web Components | Framework-agnostic | More complex build, shadow DOM limitations |
| Module Federation | Shared dependencies | Tight coupling between apps, complex webpack config |

### Rationale

micro-lc supports parcels (single-spa) and iframes. A parcel approach shares the DOM and allows the launcher to inject configuration. The app remains functional standalone in dev (just without the chrome).

### Consequences

- No `<AppBar>` or `<UserMenu>` in the finance app
- Routing must work both standalone (dev) and composed (prod)
- Theming may be injected by the launcher via CSS variables

---

## ADR-8: Tabler Icons for Category Visuals

**Status:** Accepted
**Date:** 2026-06-01
**Traces:** FR-5 (category management)

### Problem

Categories need visual icons for quick identification in dashboards and lists.

### Solution

Use Tabler Icons (`@tabler/icons-react`), storing the kebab-case icon name in the database (e.g., `"shopping-cart"`). Frontend resolves the name to the component dynamically.

### Alternatives Considered

| Alternative | Pros | Cons |
|-------------|------|------|
| Phosphor Icons | 7,800 icons, multiple weights | Extra dependency alongside Mantine's Tabler |
| Font Awesome | Well-known | Heavier setup, many are brand logos, licensing |
| Curated subset (~50 icons) | Smallest bundle | Limits user choice |

### Rationale

Mantine already ships with `@tabler/icons-react` (5,400+ icons). Zero extra dependencies, consistent design language, and sufficient range for finance categories.

### Consequences

- DB stores `VARCHAR(100)` icon identifier
- Frontend needs a dynamic icon resolver component
- UI provides a searchable picker + link to https://tabler.io/icons

---

## ADR-9: GitHub Actions for CI/CD

**Status:** Accepted
**Date:** 2026-06-01

### Problem

Need CI/CD for an open-source project hosted on GitHub.

### Solution

GitHub Actions with workflows for:
- Backend: build, test, lint (Gradle)
- Frontend: build, test, lint (npm)
- Integration: Docker Compose up + API tests

### Alternatives Considered

| Alternative | Pros | Cons |
|-------------|------|------|
| GitLab CI | More powerful pipelines | Project is on GitHub |
| CircleCI | Fast, good caching | Extra service, less GitHub integration |
| Self-hosted Jenkins | Full control | Maintenance overhead, overkill for personal project |

### Rationale

GitHub Actions is native to the hosting platform, free for open-source, and has excellent Docker/Gradle/Node support.

### Consequences

- Workflows defined in `.github/workflows/`
- Secrets managed via GitHub repository settings
- Docker Compose available in GitHub Actions runners for integration tests
