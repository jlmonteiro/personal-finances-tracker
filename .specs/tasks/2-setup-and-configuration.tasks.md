# Tasks: Setup & Configuration

## Epic

- **Epic ID**: EPIC-2
- **Status**: In Progress

## References

| ID | Name |
|-----|------|
| [FR-1](../requirements/setup.md#fr-1) | Setup Wizard |
| [FR-2](../requirements/setup.md#fr-2) | Currency Configuration |
| [FR-3](../requirements/setup.md#fr-3) | Month Start Day |
| [FR-4](../requirements/setup.md#fr-4) | Income Configuration |
| [FR-17](../requirements/setup.md#fr-17) | Income Payment Date Adjustment |
| [UJ-1](../requirements/setup.md#uj-1) | First-Time Setup |
| [ADR-6](../design/adrs.md#adr-6) | Liquibase + JOOQ |

## User Stories

### Story 1: Configuration API (Backend)

- **Story ID**: STORY-4
- **Estimate**: 4h
- **Description**: Implement the configuration REST endpoints. The `app_configuration` table already exists. Add the service, mapper, and controller layers.
- **Dependencies**: STORY-2 (JOOQ codegen)

**Deliverables:**
- `ConfigurationController.kt` — REST endpoints (GET, POST, PATCH)
- `ConfigurationService.kt` — Business logic
- `ConfigurationDto.kt` — Kotlin data class DTO
- `ConfigurationMapper.kt` — MapStruct mapper (JOOQ record ↔ DTO)
- Cucumber feature file + step definitions for configuration

**Acceptance Criteria:**
- [ ] `GET /api/v1/configuration` returns 404 when no config exists
- [ ] `GET /api/v1/configuration` returns 200 with config when it exists
- [ ] `POST /api/v1/configuration` creates config and returns 201
- [ ] `POST /api/v1/configuration` returns 409 if config already exists
- [ ] `PATCH /api/v1/configuration` updates existing config and returns 200
- [ ] Validation: currency is 3-char ISO code, monthStartDay between 1-28
- [ ] Tests pass with Testcontainers

---

### Story 2: Income Sources API (Backend)

- **Story ID**: STORY-5
- **Estimate**: 6h
- **Description**: Create the `income_sources` table migration and implement CRUD endpoints.
- **Dependencies**: STORY-4

**Deliverables:**
- `002-income-sources.yaml` — Liquibase migration
- `IncomeSourceController.kt` — REST endpoints (GET list, POST, PATCH, DELETE)
- `IncomeSourceService.kt` — Business logic
- `IncomeSourceDto.kt` — Kotlin data class DTO
- `IncomeSourceMapper.kt` — MapStruct mapper
- Cucumber feature file + step definitions

**Acceptance Criteria:**
- [ ] `GET /api/v1/income-sources` returns paginated list
- [ ] `POST /api/v1/income-sources` creates and returns 201
- [ ] `PATCH /api/v1/income-sources/{id}` updates and returns 200
- [ ] `DELETE /api/v1/income-sources/{id}` returns 204
- [ ] Validation: amount > 0, frequency in allowed values, payment date type valid
- [ ] Supports fixed and relative payment date rules
- [ ] JOOQ codegen regenerates with new table
- [ ] Tests pass with Testcontainers

---

### Story 3: Setup Wizard Frontend (Steps 1-2)

- **Story ID**: STORY-6
- **Estimate**: 4h
- **Description**: Implement the setup wizard UI for currency selection and month start day. Includes routing guard (redirect to /setup if no config).
- **Dependencies**: STORY-4

**Deliverables:**
- `Setup.tsx` — Wizard page with stepper
- `useConfiguration.ts` — Hook to fetch/create configuration
- API client functions for configuration endpoints
- React Router setup with navigation guard
- Mantine Stepper component for wizard flow

**Acceptance Criteria:**
- [ ] App redirects to `/setup` when `GET /api/v1/configuration` returns 404
- [ ] Step 1: Currency dropdown with common currencies (EUR, GBP, USD, CHF, etc.)
- [ ] Step 2: Month start day selector (1-28, default 1)
- [ ] Back/Next navigation between steps
- [ ] Form validation (currency required, day in range)
- [ ] Matches wireframe layout (setup-step1.ui.md, setup-step2.ui.md)

---

### Story 4: Setup Wizard Frontend (Step 3 - Income Sources)

- **Story ID**: STORY-7
- **Estimate**: 5h
- **Description**: Implement step 3 of the setup wizard — adding income sources. Includes the income source form with frequency and payment date configuration.
- **Dependencies**: STORY-5, STORY-6

**Deliverables:**
- Income source form component (name, amount, frequency, date type, date rule, start/end date)
- Table showing added income sources with edit/delete
- "Finish Setup" button that saves config + redirects to dashboard
- API client functions for income source endpoints

**Acceptance Criteria:**
- [ ] User can add one or more income sources
- [ ] Table displays added sources with edit/delete actions
- [ ] Frequency dropdown: Monthly, Weekly, Fortnightly, Four-Weekly
- [ ] Date type toggle: Fixed / Relative
- [ ] Fixed: day of month input (1-28)
- [ ] Relative: dropdown with options (every Monday, last Thursday, first Friday, etc.)
- [ ] Start date required, end date optional
- [ ] "Finish Setup" persists configuration and redirects to `/`
- [ ] Matches wireframe layout (setup-step3.ui.md)

---

### Story 5: Settings Page

- **Story ID**: STORY-8
- **Estimate**: 3h
- **Description**: Implement a settings page to update configuration and manage income sources after initial setup.
- **Dependencies**: STORY-7

**Deliverables:**
- `Settings.tsx` — Settings page with configuration form and income sources management
- Sidebar navigation entry for Settings
- Reuses income source form component from Story 4

**Acceptance Criteria:**
- [ ] Settings page accessible from sidebar navigation
- [ ] Can update currency and month start day (PATCH /api/v1/configuration)
- [ ] Can add/edit/delete income sources
- [ ] Success toast on save, error toast on failure
- [ ] Reuses shared components from the wizard

---

## Story Breakdown Guidelines

- Stories 1-2 are backend-only (API + DB)
- Stories 3-4 are frontend (wizard UI)
- Story 5 ties it together (settings page, reuses wizard components)
- FR-17 (income payment date adjustment) is deferred to EPIC-4 (monthly planning) since it requires financial months to exist
