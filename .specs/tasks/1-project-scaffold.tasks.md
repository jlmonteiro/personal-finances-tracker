# Tasks: Project Scaffold & CI Foundation

## Epic

- **Epic ID**: EPIC-1
- **Status**: Draft

## References

| ID | Name |
|-----|------|
| [ADR-1](../design/adrs.md#adr-1) | Multi-Module Gradle Build |
| [ADR-2](../design/adrs.md#adr-2) | Spring Boot 4.x with Kotlin |
| [ADR-3](../design/adrs.md#adr-3) | React + Mantine + Vite |
| [ADR-5](../design/adrs.md#adr-5) | PostgreSQL with Docker Compose |
| [ADR-6](../design/adrs.md#adr-6) | Liquibase + JOOQ |
| [ADR-9](../design/adrs.md#adr-9) | GitHub Actions CI/CD |

## User Stories

### Story 1: Gradle Multi-Module Project Skeleton

- **Story ID**: STORY-1
- **Estimate**: 4h
- **Description**: Create the bare Gradle multi-module project structure with backend and frontend modules. Ready to import in IntelliJ with no errors.
- **Dependencies**: None

**Deliverables:**
```
personal-finances-tracker/
├── settings.gradle.kts           # Includes backend + frontend modules
├── build.gradle.kts              # Root: shared config, repositories
├── gradle/
│   ├── wrapper/
│   │   ├── gradle-wrapper.jar
│   │   └── gradle-wrapper.properties
│   └── libs.versions.toml        # Version catalog
├── gradlew
├── gradlew.bat
├── backend/
│   ├── build.gradle.kts          # Spring Boot 4.x, Kotlin, JOOQ, Liquibase, MapStruct
│   └── src/
│       ├── main/
│       │   ├── kotlin/com/personal/finances/
│       │   │   └── Application.kt   # @SpringBootApplication (empty)
│       │   └── resources/
│       │       └── application.yml   # Minimal config (server port, spring docker compose)
│       └── test/
│           └── kotlin/com/personal/finances/
│               └── ApplicationTest.kt  # Context loads test
├── frontend/
│   ├── build.gradle.kts          # Node-gradle plugin (for CI builds)
│   ├── package.json              # React, Mantine, Vite, TypeScript
│   ├── vite.config.ts            # Proxy /api → localhost:8080
│   ├── tsconfig.json
│   └── src/
│       ├── App.tsx               # Minimal "Hello World" component
│       └── main.tsx              # Entry point
├── docker-compose.yml            # PostgreSQL 17, volume at ~/.personal-finances-app/volumes/database
├── .gitignore
└── README.md                     # Project overview, how to run
```

**Acceptance Criteria:**
- [ ] `./gradlew build` succeeds with no errors
- [ ] Backend module compiles Kotlin and Spring Boot context loads in test
- [ ] Frontend module builds via `npm run build` (triggered by Gradle or standalone)
- [ ] `docker-compose up` starts PostgreSQL 17 with persistent volume
- [ ] Project imports cleanly in IntelliJ IDEA (no unresolved dependencies)
- [ ] Version catalog (`libs.versions.toml`) defines all dependency versions
- [ ] `.gitignore` covers Gradle, Node, IDE, and build artifacts

---

### Story 2: Liquibase + JOOQ Code Generation Pipeline

- **Story ID**: STORY-2
- **Estimate**: 4h
- **Description**: Set up Liquibase migrations and JOOQ code generation in the backend module. Create an initial empty changelog. Verify JOOQ generates code from the schema after migrations run.
- **Dependencies**: STORY-1

**Deliverables:**
- `backend/src/main/resources/db/changelog/db.changelog-master.yaml` — Liquibase master changelog
- `backend/src/main/resources/db/changelog/001-initial-schema.yaml` — Empty placeholder migration
- Gradle task: `jooqCodegen` depends on Liquibase migration (runs against Testcontainers or Docker DB)
- JOOQ generated sources output to `backend/build/generated/jooq`
- `backend/build.gradle.kts` configured with JOOQ plugin + Liquibase plugin

**Acceptance Criteria:**
- [ ] `./gradlew :backend:jooqCodegen` runs Liquibase then generates JOOQ classes
- [ ] Generated sources are on the compile classpath
- [ ] Backend still compiles and context loads test passes
- [ ] No generated code committed to git (in `.gitignore`)

---

### Story 3: GitHub Actions CI Pipeline

- **Story ID**: STORY-3
- **Estimate**: 2h
- **Description**: Create GitHub Actions workflows for backend and frontend. Runs on every push and PR.
- **Dependencies**: STORY-1

**Deliverables:**
- `.github/workflows/backend.yml` — Build + test backend (Gradle, JDK 21, Docker for Testcontainers)
- `.github/workflows/frontend.yml` — Build + lint + test frontend (Node 20)

**Acceptance Criteria:**
- [ ] Backend workflow: checkout → setup JDK 21 → `./gradlew :backend:build` → test results uploaded
- [ ] Frontend workflow: checkout → setup Node 20 → `npm ci` → `npm run lint` → `npm run build` → `npm run test`
- [ ] Both workflows trigger on push to `main` and on pull requests
- [ ] Workflows pass on a clean repository

---

## Story Breakdown Guidelines

- Story 1 is the **first commit** — bare skeleton, importable, buildable
- Story 2 adds the data layer pipeline (no actual tables yet — those come in EPIC-2)
- Story 3 ensures CI is green from day one
- Stories 2 and 3 can be worked in parallel (both depend only on Story 1)
