# Design Index

## Architecture Overview

Personal Finances Tracker is a micro-frontend application composed of a Kotlin/Spring Boot backend API and a React/Mantine frontend, designed to be loaded by a micro-lc launcher.

```mermaid
graph TB
    subgraph Launcher["micro-lc Launcher"]
        Shell[Shell / Chrome]
        OAuth[Google OAuth2]
    end

    subgraph FinanceApp["Personal Finances Tracker"]
        FE[Frontend<br/>React + Mantine + Vite]
        BE[Backend<br/>Spring Boot 4.x + Kotlin]
        DB[(PostgreSQL)]
    end

    Shell --> FE
    OAuth --> Shell
    FE -->|REST /api + Bearer Token| BE
    BE --> DB
```

## Pages

| Page | Description |
|------|-------------|
| [adrs.md](adrs.md) | Architecture Decision Records |
| [data-models.md](data-models.md) | Database schemas and entity relationships |
| [rest-architecture.md](rest-architecture.md) | REST API design and endpoints |
| [frontend-architecture.md](frontend-architecture.md) | Frontend structure, routing, and component design |
| [ui-testing.md](ui-testing.md) | UI testing guidelines and strategy |
| [test-scenarios.md](test-scenarios.md) | Test scenarios validating requirements |

## Component Interaction

```mermaid
graph LR
    subgraph Frontend
        Pages[Pages / Views]
        Store[State Management]
        API[API Client]
    end

    subgraph Backend
        Controllers[REST Controllers]
        Services[Service Layer]
        Mappers[MapStruct Mappers]
        JOOQ[JOOQ Repositories]
    end

    subgraph Infrastructure
        PG[(PostgreSQL)]
        LB[Liquibase Migrations]
    end

    Pages --> Store
    Store --> API
    API -->|HTTP + JWT| Controllers
    Controllers --> Services
    Services --> Mappers
    Mappers --> JOOQ
    JOOQ --> PG
    LB --> PG
```

## Key Design Decisions

| ADR | Decision | Status |
|-----|----------|--------|
| [ADR-1](adrs.md#adr-1) | Multi-module Gradle build | Accepted |
| [ADR-2](adrs.md#adr-2) | Spring Boot 4.x with Kotlin | Accepted |
| [ADR-3](adrs.md#adr-3) | React + Mantine + Vite for frontend | Accepted |
| [ADR-4](adrs.md#adr-4) | OAuth2 with dev fake identity | Accepted |
| [ADR-5](adrs.md#adr-5) | PostgreSQL with Docker Compose | Accepted |
| [ADR-6](adrs.md#adr-6) | Liquibase + JOOQ for data layer | Accepted |
| [ADR-7](adrs.md#adr-7) | Micro-frontend composition via micro-lc | Accepted |
| [ADR-8](adrs.md#adr-8) | Tabler Icons for category visuals | Accepted |
| [ADR-9](adrs.md#adr-9) | GitHub Actions for CI/CD | Accepted |
